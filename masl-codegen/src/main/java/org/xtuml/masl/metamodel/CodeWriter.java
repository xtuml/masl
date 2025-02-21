/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel;

import org.xtuml.masl.metamodel.code.*;
import org.xtuml.masl.metamodel.code.CaseStatement.Alternative;
import org.xtuml.masl.metamodel.code.IfStatement.Branch;
import org.xtuml.masl.metamodel.code.LoopSpec.FromToRange;
import org.xtuml.masl.metamodel.code.LoopSpec.TypeRange;
import org.xtuml.masl.metamodel.code.LoopSpec.VariableElements;
import org.xtuml.masl.metamodel.code.LoopSpec.VariableRange;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.PragmaDefinition;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.common.Visibility;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.exception.BuiltinException;
import org.xtuml.masl.metamodel.exception.ExceptionDeclaration;
import org.xtuml.masl.metamodel.exception.ExceptionReference;
import org.xtuml.masl.metamodel.expression.*;
import org.xtuml.masl.metamodel.expression.CreateExpression.AttributeValue;
import org.xtuml.masl.metamodel.object.*;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.*;
import org.xtuml.masl.metamodel.type.*;
import org.xtuml.masl.utils.TextUtils;

import java.io.Writer;
import java.text.BreakIterator;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.regex.Pattern;

public class CodeWriter extends ASTNodeVisitor {

    interface TextFilter {

        String apply(final String text);
    }

    static final TextFilter TO_UPPER = text -> text.toUpperCase();

    static final TextFilter TO_LOWER = text -> text.toLowerCase();

    static final TextFilter INDENT = text -> TextUtils.indentText("  ", text);

    static final TextFilter STRIP_TRAILING_WHITE = text -> text.replaceAll(" +\n", "\n");

    static TextFilter align(final String alignTo) {
        return text -> align(text, alignTo);
    }

    private static final String PRAGMA_ALIGNER = "£pragma£";
    private static final String COMMENT_ALIGNER = "£comment£";
    private static final String PARAM_ALIGNER = "£param£";
    private static final String STRUCT_ALIGNER = "£struct£";

    static final TextFilter ALIGN_COMMENT = align(COMMENT_ALIGNER);
    static final TextFilter ALIGN_PRAGMA = align(PRAGMA_ALIGNER);
    static final TextFilter ALIGN_PARAM = align(PARAM_ALIGNER);
    static final TextFilter ALIGN_STRUCT = align(STRUCT_ALIGNER);

    static TextFilter addSuffix(final String suffix) {
        return text -> text.length() > 0 ? text + suffix : "";
    }

    static TextFilter addPrefix(final String prefix) {
        return text -> text.length() > 0 ? prefix + text : "";
    }

    static TextFilter addLinePrefix(final String prefix) {
        return text -> Pattern.compile("^", Pattern.MULTILINE).matcher(text).replaceAll(prefix);
    }

    private static final String wrapComment(final String prefix, final String comment) {
        return addLinePrefix(prefix + "//! ").apply(wrap(comment, 80));
    }

    private static final String wrapComment(final String comment) {
        return wrapComment("", comment);
    }

    private final Deque<StringBuilder> codeBlocks = new ArrayDeque<>();

    private void write(final String text) {
        codeBlocks.peek().append(text);
    }

    private void pushBlock() {
        codeBlocks.push(new StringBuilder());
    }

    private String applyFilters(String text, final TextFilter... filters) {
        for (final TextFilter filter : filters) {
            text = filter.apply(text);
        }
        return text;
    }

    private void popBlock(final TextFilter... filters) {
        final StringBuilder popped = codeBlocks.pop();
        write(applyFilters(popped.toString(), filters));
    }

    private Domain currentDomain = null;
    private boolean interfaceOnly = false;

    public CodeWriter(final boolean interfaceOnly) {
        this.interfaceOnly = interfaceOnly;
    }

    public void writeDomainFile(final Writer writer, final Domain domain) throws Exception {
        final int saveMaxLineLength = TextUtils.getMaxLineLength();
        TextUtils.setMaxLineLength(120);
        currentDomain = domain;
        pushBlock();

        pushBlock();
        visit(domain);
        popBlock(STRIP_TRAILING_WHITE);

        writer.write(codeBlocks.peek().toString());

        writer.flush();
        writer.close();
        TextUtils.setMaxLineLength(saveMaxLineLength);
    }

    @Override
    public void visitAnonymousStructure(final AnonymousStructure node) {
        write("structure\n");
        pushBlock();
        int i = 0;
        for (final BasicType eltType : node.getElements()) {
            write("element_" + ++i + STRUCT_ALIGNER + " : ");
            visit(eltType);
            write(";");
        }
        popBlock(ALIGN_STRUCT, INDENT);
        write("end structure");

    }

    @Override
    public void visitAnyExpression(final AnyExpression node) {
        visit(node.getCollection());
        write("'any");
        if (node.getCount() != null) {
            write("(");
            visit(node.getCount());
            write(")");
        }

    }

    @Override
    public void visitArrayType(final ArrayType node) {

    }

    @Override
    public void visitAssignmentStatement(final AssignmentStatement node) {

    }

    @Override
    public void visitAttributeDeclaration(final AttributeDeclaration node) {

    }

    @Override
    public void visitBagType(final BagType node) {
        write("set of ");
        visit(node.getContainedType());

    }

    @Override
    public void visitBinaryExpression(final BinaryExpression node) {

    }

    @Override
    public void visitBooleanLiteral(final BooleanLiteral node) {
        write(node.getValue() ? "true" : "false");

    }

    @Override
    public void visitBuiltinException(final BuiltinException node) {

    }

    @Override
    public void visitBuiltinType(final BuiltinType node) {
        write(node.getName());

    }

    @Override
    public void visitCancelTimerStatement(final CancelTimerStatement node) {

    }

    @Override
    public void visitCaseAlternative(final Alternative node) {

    }

    @Override
    public void visitCaseStatement(final CaseStatement node) {

    }

    @Override
    public void visitCastExpression(final CastExpression node) {

    }

    @Override
    public void visitCharacterLiteral(final CharacterLiteral node) {
        write("'" + node.getValue() + "'");

    }

    @Override
    public void visitCharacteristicExpression(final CharacteristicExpression node) {

    }

    @Override
    public void visitCharacteristicRange(final CharacteristicRange node) {

    }

    @Override
    public void visitCodeBlock(final CodeBlock node) {

    }

    @Override
    public void visitConsoleLiteral(final ConsoleLiteral node) {

    }

    @Override
    public void visitConstrainedType(final ConstrainedType node) {

    }

    @Override
    public void visitCorrelatedNavExpression(final CorrelatedNavExpression node) {

    }

    @Override
    public void visitCreateAttributeValue(final AttributeValue node) {

    }

    @Override
    public void visitCreateDurationExpression(final CreateDurationExpression node) {

    }

    @Override
    public void visitCreateExpression(final CreateExpression node) {

    }

    @Override
    public void visitDelayStatement(final DelayStatement node) {

    }

    @Override
    public void visitDeleteStatement(final DeleteStatement node) {

    }

    @Override
    public void visitDeltaConstraint(final DeltaConstraint node) {

    }

    @Override
    public void visitDictionaryAccessExpression(final DictionaryAccessExpression node) {

    }

    @Override
    public void visitDictionaryContainsExpression(final DictionaryContainsExpression node) {

    }

    @Override
    public void visitDictionaryKeysExpression(final DictionaryKeysExpression node) {

    }

    @Override
    public void visitDictionaryValuesExpression(final DictionaryValuesExpression node) {

    }

    @Override
    public void visitDigitsConstraint(final DigitsConstraint node) {

    }

    @Override
    public void visitDomain(final Domain domain) {
        write("domain " + domain.getName() + " is\n");

        if (!interfaceOnly) {
            pushBlock();
            for (final ObjectDeclaration obj : domain.getObjects()) {
                write("object " + obj.getName() + ";\n");
            }
            popBlock(INDENT, addSuffix("\n\n"));
        }

        pushBlock();
        for (final TypeDeclaration type : domain.getTypeForwardDeclarations()) {
            write(getVisibility(type.getVisibility()) + " type " + type.getName() + ";\n");
        }
        popBlock(INDENT, addSuffix("\n\n"));

        pushBlock();
        visit(domain.getTypes());
        popBlock(INDENT, addSuffix("\n\n"));

        pushBlock();
        visit(domain.getExceptions());
        popBlock(INDENT, addSuffix("\n\n"));

        pushBlock();
        visit(domain.getRelationships());
        popBlock(INDENT, addSuffix("\n\n"));

        pushBlock();
        visit(domain.getServices());
        popBlock(INDENT, addSuffix("\n\n"));

        pushBlock();
        visit(domain.getTerminators());
        popBlock(INDENT, addSuffix("\n\n"));

        if (!interfaceOnly) {
            pushBlock();
            visit(domain.getObjects());
            popBlock(INDENT, addSuffix("\n\n"));
        }

        write("end domain;");
        pushBlock();
        visit(domain.getPragmas());
        popBlock();

    }

    private void visit(final Collection<? extends ASTNode> nodes,
                       final String prefix,
                       final String separator,
                       final String suffix) {
        if (nodes.size() > 0) {
            write(prefix);
        }
        final Iterator<? extends ASTNode> it = nodes.iterator();
        while (it.hasNext()) {
            visit(it.next());
            if (it.hasNext()) {
                write(separator);
            }
        }
        if (nodes.size() > 0) {
            write(suffix);
        }
    }

    @Override
    public void visitDomainFunctionInvocation(final DomainFunctionInvocation node) {
        if (node.getService().getDomain() != currentDomain) {
            write(node.getService().getDomain().getName() + "::");
        }
        write(node.getService().getName() + "(");
        visit(node.getArguments(), " ", ", ", " ");
        write(")");

    }

    @Override
    public void visitDomainService(final DomainService node) {
        if (interfaceOnly && node.getVisibility() == Visibility.PRIVATE) {

        }

        pushBlock();
        {
            if (node.getComment() != null) {
                write(wrapComment(node.getComment()));
            }
            write(getVisibility(node.getVisibility()) + " " + "service" + " " + PRAGMA_ALIGNER + node.getName() + " (");
            visit(node.getParameters(), " ", ",\n", " ");
            write(")");
            if (node.getReturnType() != null) {
                write(" return ");
                visit(node.getReturnType());
            }
            write(";\n");
            visit(node.getDeclarationPragmas());
        }
        popBlock(ALIGN_PRAGMA, ALIGN_PARAM);
        write("\n");

    }

    @Override
    public void visitDomainServiceInvocation(final DomainServiceInvocation node) {
        if (node.getService().getDomain() != currentDomain) {
            write(node.getService().getDomain().getName() + "::");
        }
        write(node.getService().getName() + "(");
        visit(node.getArguments(), " ", ", ", " ");
        write(");");

    }

    @Override
    public void visitDomainTerminator(final DomainTerminator node) {
        if (node.getComment() != null) {
            write(wrapComment(node.getComment()));
        }
        write("terminator " + node.getName() + " is\n");
        pushBlock();
        visit(node.getServices());
        popBlock(INDENT);
        write("end terminator;\n");
        visit(node.getPragmas());

    }

    @Override
    public void visitDomainTerminatorService(final DomainTerminatorService node) {
        pushBlock();
        if (node.getComment() != null) {
            write(wrapComment(node.getComment()));
        }
        write(getVisibility(node.getVisibility()) + " service " + PRAGMA_ALIGNER + node.getName() + " (");
        visit(node.getParameters(), " ", ",\n", " ");
        write(");\n");
        visit(node.getDeclarationPragmas());
        popBlock(ALIGN_PRAGMA, ALIGN_PARAM);
        write("\n");

    }

    @Override
    public void visitDurationLiteral(final DurationLiteral node) {

    }

    @Override
    public void visitElementsExpression(final ElementsExpression node) {

    }

    @Override
    public void visitEndlLiteral(final EndlLiteral node) {

    }

    @Override
    public void visitEnumerateItem(final EnumerateItem node) {
        if (node.getComment() != null) {
            write("\n" + wrapComment(node.getComment()));
        }
        write(node.getName());
        if (node.getValue() != null) {
            write(" = ");
            visit(node.getValue());
        }

        if (node != node.getEnumerate().getItems().get(node.getEnumerate().getItems().size() - 1)) {
            write(",");
        }
        write("\n");

    }

    @Override
    public void visitEnumerateLiteral(final EnumerateLiteral node) {
        if (node.getValue().getEnumerate().getTypeDeclaration().getDomain() != currentDomain) {
            write(node.getValue().getEnumerate().getTypeDeclaration().getDomain().getName() + "::");
        }
        write(node.getValue().getEnumerate().getTypeDeclaration().getName() + "." + node.getValue().getName());

    }

    @Override
    public void visitEnumerateType(final EnumerateType node) {
        write("enum (\n");
        pushBlock();
        visit(node.getItems());
        popBlock(ALIGN_COMMENT, INDENT);
        write(")");

    }

    @Override
    public void visitEofExpression(final EofExpression node) {

    }

    @Override
    public void visitEraseStatement(final EraseStatement node) {

    }

    @Override
    public void visitEventDeclaration(final EventDeclaration node) {

    }

    @Override
    public void visitEventExpression(final EventExpression node) {

    }

    @Override
    public void visitExceptionDeclaration(final ExceptionDeclaration node) {
        pushBlock();
        write(getVisibility(node.getVisibility()) + " exception" + PRAGMA_ALIGNER + node.getName() + ";\n");
        visit(node.getPragmas());
        popBlock(addSuffix("\n"), ALIGN_PRAGMA);

    }

    @Override
    public void visitExceptionHandler(final ExceptionHandler node) {

    }

    @Override
    public void visitExceptionReference(final ExceptionReference node) {

    }

    @Override
    public void visitExitStatement(final ExitStatement node) {

    }

    @Override
    public void visitFindAttributeNameExpression(final FindAttributeNameExpression node) {

    }

    @Override
    public void visitFindExpression(final FindExpression node) {

    }

    @Override
    public void visitFindParameterExpression(final FindParameterExpression node) {

    }

    @Override
    public void visitFlushLiteral(final FlushLiteral node) {

    }

    @Override
    public void visitForStatement(final ForStatement node) {

    }

    @Override
    public void visitGenerateStatement(final GenerateStatement node) {

    }

    @Override
    public void visitIOStreamStatement(final IOStreamStatement node) {

    }

    @Override
    public void visitIdentifierDeclaration(final IdentifierDeclaration node) {

    }

    @Override
    public void visitIfBranch(final Branch node) {

    }

    @Override
    public void visitIfStatement(final IfStatement node) {

    }

    @Override
    public void visitIndexedNameExpression(final IndexedNameExpression node) {

    }

    @Override
    public void visitInstanceFunctionInvocation(final InstanceFunctionInvocation node) {

    }

    @Override
    public void visitInstanceOrderingExpression(final InstanceOrderingExpression node) {

    }

    @Override
    public void visitInstanceServiceInvocation(final InstanceServiceInvocation node) {

    }

    @Override
    public void visitInstanceType(final InstanceType node) {

    }

    @Override
    public void visitIntegerLiteral(final IntegerLiteral node) {
        write(String.valueOf(node.getValue()));

    }

    @Override
    public void visitLinkUnlinkExpression(final LinkUnlinkExpression node) {

    }

    @Override
    public void visitLinkUnlinkStatement(final LinkUnlinkStatement node) {

    }

    @Override
    public void visitLoopFromToRange(final FromToRange node) {

    }

    @Override
    public void visitLoopTypeRange(final TypeRange node) {

    }

    @Override
    public void visitLoopVariableElements(final VariableElements node) {

    }

    @Override
    public void visitLoopVariableRange(final VariableRange node) {

    }

    @Override
    public void visitMinMaxRange(final MinMaxRange node) {

    }

    @Override
    public void visitNavigationExpression(final NavigationExpression node) {

    }

    @Override
    public void visitNullLiteral(final NullLiteral node) {
        write("null");

    }

    @Override
    public void visitObjectDeclaration(final ObjectDeclaration node) {

    }

    @Override
    public void visitObjectFunctionInvocation(final ObjectFunctionInvocation node) {

    }

    @Override
    public void visitObjectNameExpression(final ObjectNameExpression node) {

    }

    @Override
    public void visitObjectService(final ObjectService node) {

    }

    @Override
    public void visitObjectServiceInvocation(final ObjectServiceInvocation node) {

    }

    @Override
    public void visitParameterDefinition(final ParameterDefinition node) {
        write(PARAM_ALIGNER +
              node.getName() +
              PARAM_ALIGNER +
              " : " +
              node.getMode().toString().toLowerCase() +
              " " +
              PARAM_ALIGNER);
        visit(node.getType());

    }

    @Override
    public void visitParameterNameExpression(final ParameterNameExpression node) {

    }

    @Override
    public void visitParseExpression(final ParseExpression node) {

    }

    @Override
    public void visitPragmaDefinition(final PragmaDefinition node) {
        write(PRAGMA_ALIGNER +
              "pragma " +
              node.getName() +
              PRAGMA_ALIGNER +
              " (" +
              TextUtils.formatList(node.getValues(), "\"", "\", \"", "\"") +
              ");\n");

    }

    @Override
    public void visitPragmaList(final PragmaList node) {
        visit(node.getPragmas());

    }

    @Override
    public void visitPragmaStatement(final PragmaStatement node) {
        pushBlock();
        visit(node.getPragmas());
        popBlock(ALIGN_PRAGMA);

    }

    @Override
    public void visitRaiseStatement(final RaiseStatement node) {

    }

    @Override
    public void visitRangeConstraint(final RangeConstraint node) {

    }

    @Override
    public void visitRealLiteral(final RealLiteral node) {
        write(String.valueOf(node.getValue()));

    }

    @Override
    public void visitReferentialAttributeDefinition(final ReferentialAttributeDefinition node) {

    }

    @Override
    public void visitReturnStatement(final ReturnStatement node) {
        write("return ");
        visit(node.getReturnValue());
        write(";\n");

    }

    @Override
    public void visitScheduleStatement(final ScheduleStatement node) {

    }

    @Override
    public void visitSelectedAttributeExpression(final SelectedAttributeExpression node) {

    }

    @Override
    public void visitSelectedComponentExpression(final SelectedComponentExpression node) {

    }

    @Override
    public void visitSequenceType(final SequenceType node) {
        write("sequence ");
        if (node.getBound() != null) {
            write("(");
            visit(node.getBound());
            write(") ");
        }
        write("of ");
        visit(node.getContainedType());

    }

    @Override
    public void visitSetType(final SetType node) {
        write("set of ");
        visit(node.getContainedType());

    }

    @Override
    public void visitDictionaryType(final DictionaryType node) {
        write("dictionary ");
        visit(node.getKeyType());
        write(" of ");
        visit(node.getValueType());

    }

    @Override
    public void visitSliceExpression(final SliceExpression node) {

    }

    @Override
    public void visitSplitExpression(final SplitExpression node) {

    }

    @Override
    public void visitState(final State node) {

    }

    @Override
    public void visitStringLiteral(final StringLiteral node) {
        write("\"" + node.getValue() + "\"");

    }

    @Override
    public void visitStructureAggregate(final StructureAggregate node) {

    }

    @Override
    public void visitStructureElement(final StructureElement node) {
        if (node.getComment() != null) {
            write("\n" + wrapComment(node.getComment()));
        }
        write(node.getName() + STRUCT_ALIGNER + " : ");
        visit(node.getType());
        if (!interfaceOnly && node.getDefault() != null) {
            write(" " + STRUCT_ALIGNER + ":= ");
            visit(node.getDefault());
            write(";");
        } else {
            write(";" + STRUCT_ALIGNER);
        }
        write("\n");

    }

    @Override
    public void visitStructureOrderingExpression(final StructureOrderingExpression node) {

    }

    @Override
    public void visitStructureType(final StructureType structure) {
        write("structure\n");
        pushBlock();
        visit(structure.getElements());
        popBlock(ALIGN_STRUCT, ALIGN_COMMENT, INDENT);
        write("end structure");

    }

    @Override
    public void visitTerminatorFunctionInvocation(final TerminatorFunctionInvocation node) {

    }

    @Override
    public void visitTerminatorNameExpression(final TerminatorNameExpression node) {

    }

    @Override
    public void visitTerminatorServiceInvocation(final TerminatorServiceInvocation node) {

    }

    @Override
    public void visitThisLiteral(final ThisLiteral node) {

    }

    @Override
    public void visitTimeFieldExpression(final TimeFieldExpression node) {

    }

    @Override
    public void visitTimerFieldExpression(final TimerFieldExpression node) {

    }

    @Override
    public void visitTimestampDeltaExpression(final TimestampDeltaExpression node) {

    }

    @Override
    public void visitTimestampLiteral(final TimestampLiteral node) {

    }

    @Override
    public void visitTransitionOption(final TransitionOption node) {

    }

    @Override
    public void visitTransitionRow(final TransitionRow node) {

    }

    @Override
    public void visitTransitionTable(final TransitionTable node) {

    }

    String getVisibility(final Visibility v) {
        return v.toString().toLowerCase();
    }

    @Override
    public void visitTypeDeclaration(final TypeDeclaration type) {
        if (interfaceOnly && type.getVisibility() == Visibility.PRIVATE) {

        }

        write("\n");
        if (type.getComment() != null) {
            write(wrapComment(type.getComment()));
        }
        write(getVisibility(type.getVisibility()) + " type " + type.getName() + " is ");
        visit(type.getTypeDefinition());
        write(";\n");

    }

    @Override
    public void visitTypeNameExpression(final TypeNameExpression node) {

    }

    @Override
    public void visitUnaryExpression(final UnaryExpression node) {
        switch (node.getOperator()) {
            case ABS:
                write("abs ");
                break;
            case NOT:
                write("not ");
                break;
            case MINUS:
                write("-");
                break;
            case PLUS:
                write("+");
                break;
        }
        visit(node.getRhs());

    }

    @Override
    public void visitUnconstrainedArraySubtype(final UnconstrainedArraySubtype node) {

    }

    @Override
    public void visitUnconstrainedArrayType(final UnconstrainedArrayType node) {

    }

    @Override
    public void visitUserDefinedType(final UserDefinedType node) {
        if (node.getDomain() != currentDomain) {
            write(node.getDomain().getName() + "::");
        }
        write(node.getName());

    }

    @Override
    public void visitVariableDefinition(final VariableDefinition node) {

    }

    @Override
    public void visitVariableNameExpression(final VariableNameExpression node) {

    }

    @Override
    public void visitWhileStatement(final WhileStatement node) {

    }

    @Override
    public void vistAssociativeRelationshipDeclaration(final AssociativeRelationshipDeclaration node) {

    }

    @Override
    public void vistNormalRelationshipDeclaration(final NormalRelationshipDeclaration node) {
        pushBlock();
        write("relationship" + node.getName() + " is");
        visit(node.getLeftToRightSpec());
        write("\n");
        visit(node.getRightToLeftSpec());
        write("\n");
        visit(node.getPragmas());
        popBlock(ALIGN_PRAGMA);

    }

    @Override
    public void vistRelationshipSpecification(final RelationshipSpecification node) {

    }

    @Override
    public void vistSubtypeRelationshipDeclaration(final SubtypeRelationshipDeclaration node) {

    }

    private static final String
            SPACES =
            "                                                                                                                                                                                                  ";

    private static String align(final String text, final String delimiter) {
        final String[] lines = text.split("\n");
        final String[][] lineCols = new String[lines.length][];
        int noCols = 0;
        for (int l = 0; l < lines.length; ++l) {
            lineCols[l] = lines[l].split(delimiter, -1);
            noCols = Math.max(noCols, lineCols[l].length - 1);
        }
        final int[] colWidths = new int[noCols];
        for (final String[] lineCol : lineCols) {
            for (int c = 0; c < lineCol.length - 1; ++c) {
                colWidths[c] = Math.max(colWidths[c], lineCol[c].length());
            }
        }
        final StringBuilder result = new StringBuilder();
        for (final String[] lineCol : lineCols) {
            result.append(lineCol[0]);
            for (int c = 1; c < lineCol.length; ++c) {
                result.append(SPACES, 0, colWidths[c - 1] - lineCol[c - 1].length());
                result.append(lineCol[c]);
            }
            result.append("\n");
        }
        return result.toString();
    }

    private static String wrap(final String text, final int width) {
        final StringBuilder result = new StringBuilder();

        for (final String line : text.split("\n")) {

            final BreakIterator it = BreakIterator.getLineInstance();
            it.setText(line);
            int pos = 0;
            String trimmings = "";
            for (int start = it.first(), end = it.next(); end != BreakIterator.DONE; start = end, end = it.next()) {
                final String word = line.substring(start, end);
                final String trimmed = word.trim();
                if (pos + trimmings.length() + trimmed.length() > width) {
                    result.append("\n");
                    pos = 0;
                } else {
                    result.append(trimmings);
                    pos += trimmings.length();
                }

                result.append(trimmed);
                pos += trimmed.length();
                trimmings = word.substring(trimmed.length());
            }
            result.append("\n");
        }
        return result.toString();
    }

    public static void main(final String[] args) {
        System.out.println(wrap(
                "Merely corroberative detail, intended to give artistic verisimilitude to an otherwise bald an unconvincing narrative.\nI can trace my ancestry back to a proto-plasmal primordial atomic globule. Consequently my family pride is something inconceiveable. I can't help it, I was born sneering.\n\n\n",
                80));
    }

}
