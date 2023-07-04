/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
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

public class CodeWriter extends AbstractASTNodeVisitor<Void, Void> {

    interface TextFilter {

        String apply(final String text);
    }

    static final TextFilter TO_UPPER = new TextFilter() {

        @Override
        public String apply(final String text) {
            return text.toUpperCase();
        }
    };

    static final TextFilter TO_LOWER = new TextFilter() {

        @Override
        public String apply(final String text) {
            return text.toLowerCase();
        }
    };

    static final TextFilter INDENT = new TextFilter() {

        @Override
        public String apply(final String text) {
            return TextUtils.indentText("  ", text);
        }
    };

    static final TextFilter STRIP_TRAILING_WHITE = new TextFilter() {

        @Override
        public String apply(final String text) {
            return text.replaceAll(" +\n", "\n");
        }
    };

    static TextFilter align(final String alignTo) {
        return new TextFilter() {

            @Override
            public String apply(final String text) {
                return align(text, alignTo);
            }
        };
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
        return new TextFilter() {

            @Override
            public String apply(final String text) {
                return text.length() > 0 ? text + suffix : "";
            }
        };
    }

    static TextFilter addPrefix(final String prefix) {
        return new TextFilter() {

            @Override
            public String apply(final String text) {
                return text.length() > 0 ? prefix + text : "";
            }
        };
    }

    static TextFilter addLinePrefix(final String prefix) {
        return new TextFilter() {

            @Override
            public String apply(final String text) {
                return Pattern.compile("^", Pattern.MULTILINE).matcher(text).replaceAll(prefix);
            }
        };
    }

    private static final String wrapComment(final String prefix, final String comment) {
        return addLinePrefix(prefix + "// ").apply(wrap(comment, 80));
    }

    private static final String wrapComment(final String comment) {
        return wrapComment("", comment);
    }

    private final Deque<StringBuilder> codeBlocks = new ArrayDeque<StringBuilder>();

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
    public Void visitAnonymousStructure(final AnonymousStructure node, final Void p) throws Exception {
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

        return null;
    }

    @Override
    public Void visitAnyExpression(final AnyExpression node, final Void p) throws Exception {
        visit(node.getCollection());
        write("'any");
        if (node.getCount() != null) {
            write("(");
            visit(node.getCount());
            write(")");
        }
        return null;
    }

    @Override
    public Void visitArrayType(final ArrayType node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitAssignmentStatement(final AssignmentStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitAttributeDeclaration(final AttributeDeclaration node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitBagType(final BagType node, final Void p) throws Exception {
        write("set of ");
        visit(node.getContainedType());
        return null;
    }

    @Override
    public Void visitBinaryExpression(final BinaryExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitBooleanLiteral(final BooleanLiteral node, final Void p) throws Exception {
        write(node.getValue() ? "true" : "false");
        return null;
    }

    @Override
    public Void visitBuiltinException(final BuiltinException node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitBuiltinType(final BuiltinType node, final Void p) throws Exception {
        write(node.getName());
        return null;
    }

    @Override
    public Void visitCancelTimerStatement(final CancelTimerStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitCaseAlternative(final Alternative node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitCaseStatement(final CaseStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitCastExpression(final CastExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitCharacterLiteral(final CharacterLiteral node, final Void p) throws Exception {
        write("'" + node.getValue() + "'");
        return null;
    }

    @Override
    public Void visitCharacteristicExpression(final CharacteristicExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitCharacteristicRange(final CharacteristicRange node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitCodeBlock(final CodeBlock node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitConsoleLiteral(final ConsoleLiteral node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitConstrainedType(final ConstrainedType node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitCorrelatedNavExpression(final CorrelatedNavExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitCreateAttributeValue(final AttributeValue node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitCreateDurationExpression(final CreateDurationExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitCreateExpression(final CreateExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitDelayStatement(final DelayStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitDeleteStatement(final DeleteStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitDeltaConstraint(final DeltaConstraint node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitDictionaryAccessExpression(final DictionaryAccessExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitDictionaryContainsExpression(final DictionaryContainsExpression node, final Void p) throws
                                                                                                         Exception {
        return null;
    }

    @Override
    public Void visitDictionaryKeysExpression(final DictionaryKeysExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitDictionaryValuesExpression(final DictionaryValuesExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitDigitsConstraint(final DigitsConstraint node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitDomain(final Domain domain, final Void p) throws Exception {
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

        return null;
    }

    private void visit(final Collection<? extends ASTNode> nodes,
                       final String prefix,
                       final String separator,
                       final String suffix) throws Exception {
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
    public Void visitDomainFunctionInvocation(final DomainFunctionInvocation node, final Void p) throws Exception {
        if (node.getService().getDomain() != currentDomain) {
            write(node.getService().getDomain().getName() + "::");
        }
        write(node.getService().getName() + "(");
        visit(node.getArguments(), " ", ", ", " ");
        write(")");
        return null;
    }

    @Override
    public Void visitDomainService(final DomainService node, final Void p) throws Exception {
        if (interfaceOnly && node.getVisibility() == Visibility.PRIVATE) {
            return null;
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
        return null;
    }

    @Override
    public Void visitDomainServiceInvocation(final DomainServiceInvocation node, final Void p) throws Exception {
        if (node.getService().getDomain() != currentDomain) {
            write(node.getService().getDomain().getName() + "::");
        }
        write(node.getService().getName() + "(");
        visit(node.getArguments(), " ", ", ", " ");
        write(");");
        return null;
    }

    @Override
    public Void visitDomainTerminator(final DomainTerminator node, final Void p) throws Exception {
        if (node.getComment() != null) {
            write(wrapComment(node.getComment()));
        }
        write("terminator " + node.getName() + " is\n");
        pushBlock();
        visit(node.getServices());
        popBlock(INDENT);
        write("end terminator;\n");
        write("pragma key_letter (\"" + node.getKeyLetters() + "\");\n");
        visit(node.getPragmas());
        return null;
    }

    @Override
    public Void visitDomainTerminatorService(final DomainTerminatorService node, final Void p) throws Exception {
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
        return null;
    }

    @Override
    public Void visitDurationLiteral(final DurationLiteral node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitElementsExpression(final ElementsExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitEndlLiteral(final EndlLiteral node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitEnumerateItem(final EnumerateItem node, final Void p) throws Exception {
        write(node.getName());
        if (node.getValue() != null) {
            write(" = ");
            visit(node.getValue());
        }

        if (node != node.getEnumerate().getItems().get(node.getEnumerate().getItems().size() - 1)) {
            write(",");
        }
        if (node.getComment() != null) {
            write(wrapComment(COMMENT_ALIGNER, node.getComment()));
        } else {
            write("\n");
        }
        return null;
    }

    @Override
    public Void visitEnumerateLiteral(final EnumerateLiteral node, final Void p) throws Exception {
        if (node.getValue().getEnumerate().getTypeDeclaration().getDomain() != currentDomain) {
            write(node.getValue().getEnumerate().getTypeDeclaration().getDomain().getName() + "::");
        }
        write(node.getValue().getEnumerate().getTypeDeclaration().getName() + "." + node.getValue().getName());
        return null;
    }

    @Override
    public Void visitEnumerateType(final EnumerateType node, final Void p) throws Exception {
        write("enum (\n");
        pushBlock();
        visit(node.getItems());
        popBlock(ALIGN_COMMENT, INDENT);
        write(")");
        return null;
    }

    @Override
    public Void visitEofExpression(final EofExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitEraseStatement(final EraseStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitEventDeclaration(final EventDeclaration node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitEventExpression(final EventExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitExceptionDeclaration(final ExceptionDeclaration node, final Void p) throws Exception {
        pushBlock();
        write(getVisibility(node.getVisibility()) + " exception" + PRAGMA_ALIGNER + node.getName() + ";\n");
        visit(node.getPragmas());
        popBlock(addSuffix("\n"), ALIGN_PRAGMA);

        return null;
    }

    @Override
    public Void visitExceptionHandler(final ExceptionHandler node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitExceptionReference(final ExceptionReference node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitExitStatement(final ExitStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitFindAttributeNameExpression(final FindAttributeNameExpression node, final Void p) throws
                                                                                                       Exception {
        return null;
    }

    @Override
    public Void visitFindExpression(final FindExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitFindParameterExpression(final FindParameterExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitFlushLiteral(final FlushLiteral node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitForStatement(final ForStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitGenerateStatement(final GenerateStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitIOStreamStatement(final IOStreamStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitIdentifierDeclaration(final IdentifierDeclaration node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitIfBranch(final Branch node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitIfStatement(final IfStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitIndexedNameExpression(final IndexedNameExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitInstanceFunctionInvocation(final InstanceFunctionInvocation node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitInstanceOrderingExpression(final InstanceOrderingExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitInstanceServiceInvocation(final InstanceServiceInvocation node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitInstanceType(final InstanceType node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitIntegerLiteral(final IntegerLiteral node, final Void p) throws Exception {
        write(String.valueOf(node.getValue()));
        return null;
    }

    @Override
    public Void visitLinkUnlinkExpression(final LinkUnlinkExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitLinkUnlinkStatement(final LinkUnlinkStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitLoopFromToRange(final FromToRange node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitLoopTypeRange(final TypeRange node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitLoopVariableElements(final VariableElements node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitLoopVariableRange(final VariableRange node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitMinMaxRange(final MinMaxRange node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitNavigationExpression(final NavigationExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitNullLiteral(final NullLiteral node, final Void p) throws Exception {
        write("null");
        return null;
    }

    @Override
    public Void visitObjectDeclaration(final ObjectDeclaration node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitObjectFunctionInvocation(final ObjectFunctionInvocation node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitObjectNameExpression(final ObjectNameExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitObjectService(final ObjectService node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitObjectServiceInvocation(final ObjectServiceInvocation node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitParameterDefinition(final ParameterDefinition node, final Void p) throws Exception {
        write(PARAM_ALIGNER +
              node.getName() +
              PARAM_ALIGNER +
              " : " +
              node.getMode().toString().toLowerCase() +
              " " +
              PARAM_ALIGNER);
        visit(node.getType());
        return null;
    }

    @Override
    public Void visitParameterNameExpression(final ParameterNameExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitParseExpression(final ParseExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitPragmaDefinition(final PragmaDefinition node, final Void p) throws Exception {
        write(PRAGMA_ALIGNER +
              "pragma " +
              node.getName() +
              PRAGMA_ALIGNER +
              " (" +
              TextUtils.formatList(node.getValues(), "\"", "\", \"", "\"") +
              ");\n");
        return null;
    }

    @Override
    public Void visitPragmaList(final PragmaList node, final Void p) throws Exception {
        visit(node.getPragmas());
        return null;
    }

    @Override
    public Void visitPragmaStatement(final PragmaStatement node, final Void p) throws Exception {
        pushBlock();
        visit(node.getPragmas());
        popBlock(ALIGN_PRAGMA);
        return null;
    }

    @Override
    public Void visitRaiseStatement(final RaiseStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitRangeConstraint(final RangeConstraint node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitRealLiteral(final RealLiteral node, final Void p) throws Exception {
        write(String.valueOf(node.getValue()));
        return null;
    }

    @Override
    public Void visitReferentialAttributeDefinition(final ReferentialAttributeDefinition node, final Void p) throws
                                                                                                             Exception {
        return null;
    }

    @Override
    public Void visitReturnStatement(final ReturnStatement node, final Void p) throws Exception {
        write("return ");
        visit(node.getReturnValue());
        write(";\n");
        return null;
    }

    @Override
    public Void visitScheduleStatement(final ScheduleStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitSelectedAttributeExpression(final SelectedAttributeExpression node, final Void p) throws
                                                                                                       Exception {
        return null;
    }

    @Override
    public Void visitSelectedComponentExpression(final SelectedComponentExpression node, final Void p) throws
                                                                                                       Exception {
        return null;
    }

    @Override
    public Void visitSequenceType(final SequenceType node, final Void p) throws Exception {
        write("sequence ");
        if (node.getBound() != null) {
            write("(");
            visit(node.getBound());
            write(") ");
        }
        write("of ");
        visit(node.getContainedType());
        return null;
    }

    @Override
    public Void visitSetType(final SetType node, final Void p) throws Exception {
        write("set of ");
        visit(node.getContainedType());
        return null;
    }

    @Override
    public Void visitDictionaryType(final DictionaryType node, final Void p) throws Exception {
        write("dictionary ");
        visit(node.getKeyType());
        write(" of ");
        visit(node.getValueType());
        return null;
    }

    @Override
    public Void visitSliceExpression(final SliceExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitSplitExpression(final SplitExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitState(final State node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitStringLiteral(final StringLiteral node, final Void p) throws Exception {
        write("\"" + node.getValue() + "\"");
        return null;
    }

    @Override
    public Void visitStructureAggregate(final StructureAggregate node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitStructureElement(final StructureElement node, final Void p) throws Exception {
        write(node.getName() + STRUCT_ALIGNER + " : ");
        visit(node.getType());
        if (!interfaceOnly && node.getDefault() != null) {
            write(" " + STRUCT_ALIGNER + ":= ");
            visit(node.getDefault());
            write(";");
        } else {
            write(";" + STRUCT_ALIGNER);
        }
        if (node.getComment() != null) {
            write(" " + wrapComment(COMMENT_ALIGNER, node.getComment()));
        } else {
            write("\n");
        }
        return null;
    }

    @Override
    public Void visitStructureOrderingExpression(final StructureOrderingExpression node, final Void p) throws
                                                                                                       Exception {
        return null;
    }

    @Override
    public Void visitStructureType(final StructureType structure, final Void p) throws Exception {
        write("structure\n");
        pushBlock();
        visit(structure.getElements());
        popBlock(ALIGN_STRUCT, ALIGN_COMMENT, INDENT);
        write("end structure");
        return null;
    }

    @Override
    public Void visitTerminatorFunctionInvocation(final TerminatorFunctionInvocation node, final Void p) throws
                                                                                                         Exception {
        return null;
    }

    @Override
    public Void visitTerminatorNameExpression(final TerminatorNameExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitTerminatorServiceInvocation(final TerminatorServiceInvocation node, final Void p) throws
                                                                                                       Exception {
        return null;
    }

    @Override
    public Void visitThisLiteral(final ThisLiteral node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitTimeFieldExpression(final TimeFieldExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitTimerFieldExpression(final TimerFieldExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitTimestampDeltaExpression(final TimestampDeltaExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitTimestampLiteral(final TimestampLiteral node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitTransitionOption(final TransitionOption node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitTransitionRow(final TransitionRow node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitTransitionTable(final TransitionTable node, final Void p) throws Exception {
        return null;
    }

    String getVisibility(final Visibility v) {
        return v.toString().toLowerCase();
    }

    @Override
    public Void visitTypeDeclaration(final TypeDeclaration type, final Void p) throws Exception {
        if (interfaceOnly && type.getVisibility() == Visibility.PRIVATE) {
            return null;
        }

        write("\n");
        if (type.getComment() != null) {
            write(wrapComment(type.getComment()));
        }
        write(getVisibility(type.getVisibility()) + " type " + type.getName() + " is ");
        visit(type.getTypeDefinition());
        write(";\n");
        return null;
    }

    @Override
    public Void visitTypeNameExpression(final TypeNameExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitUnaryExpression(final UnaryExpression node, final Void p) throws Exception {
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
        return null;
    }

    @Override
    public Void visitUnconstrainedArraySubtype(final UnconstrainedArraySubtype node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitUnconstrainedArrayType(final UnconstrainedArrayType node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitUserDefinedType(final UserDefinedType node, final Void p) throws Exception {
        if (node.getDomain() != currentDomain) {
            write(node.getDomain().getName() + "::");
        }
        write(node.getName());
        return null;
    }

    @Override
    public Void visitVariableDefinition(final VariableDefinition node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitVariableNameExpression(final VariableNameExpression node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void visitWhileStatement(final WhileStatement node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void vistAssociativeRelationshipDeclaration(final AssociativeRelationshipDeclaration node,
                                                       final Void p) throws Exception {
        return null;
    }

    @Override
    public Void vistNormalRelationshipDeclaration(final NormalRelationshipDeclaration node, final Void p) throws
                                                                                                          Exception {
        pushBlock();
        write("relationship" + node.getName() + " is");
        visit(node.getLeftToRightSpec());
        write("\n");
        visit(node.getRightToLeftSpec());
        write("\n");
        visit(node.getPragmas());
        popBlock(ALIGN_PRAGMA);
        return null;
    }

    @Override
    public Void vistRelationshipSpecification(final RelationshipSpecification node, final Void p) throws Exception {
        return null;
    }

    @Override
    public Void vistSubtypeRelationshipDeclaration(final SubtypeRelationshipDeclaration node, final Void p) throws
                                                                                                            Exception {
        return null;
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
