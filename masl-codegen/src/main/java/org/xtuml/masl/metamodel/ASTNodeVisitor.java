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
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.PragmaDefinition;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.exception.BuiltinException;
import org.xtuml.masl.metamodel.exception.ExceptionDeclaration;
import org.xtuml.masl.metamodel.exception.ExceptionReference;
import org.xtuml.masl.metamodel.expression.*;
import org.xtuml.masl.metamodel.object.*;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.metamodel.project.ProjectTerminator;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.*;
import org.xtuml.masl.metamodel.type.*;
import org.xtuml.masl.metamodelImpl.expression.StructureOrderingExpression.Component;

import java.util.Collection;

public abstract class ASTNodeVisitor {

    public void visit(final ASTNode node) {
        if (node != null) {
            node.accept(this);
        }
    }

    public final void visit(final Collection<? extends ASTNode> nodes) {
        nodes.stream().forEach(this::visit);
    }

    public void visitAnonymousStructure(final AnonymousStructure node) {
    }

    public void visitAnyExpression(final AnyExpression node) {
    }

    public void visitArrayType(final ArrayType node) {
    }

    public void visitAssignmentStatement(final AssignmentStatement node) {
    }

    public void visitAttributeDeclaration(final AttributeDeclaration node) {
    }

    public void visitBagType(final BagType node) {
    }

    public void visitBinaryExpression(final BinaryExpression node) {
    }

    public void visitBooleanLiteral(final BooleanLiteral node) {
    }

    public void visitBuiltinException(final BuiltinException node) {
    }

    public void visitBuiltinType(final BuiltinType node) {
    }

    public void visitCancelTimerStatement(final CancelTimerStatement node) {
    }

    public void visitCaseAlternative(final CaseStatement.Alternative node) {
    }

    public void visitCaseStatement(final CaseStatement node) {
    }

    public void visitCastExpression(final CastExpression node) {
    }

    public void visitCharacteristicExpression(final CharacteristicExpression node) {
    }

    public void visitCharacteristicRange(final CharacteristicRange node) {
    }

    public void visitCharacterLiteral(final CharacterLiteral node) {
    }

    public void visitCodeBlock(final CodeBlock node) {
    }

    public void visitConsoleLiteral(final ConsoleLiteral node) {
    }

    public void visitConstrainedType(final ConstrainedType node) {
    }

    public void visitCorrelatedNavExpression(final CorrelatedNavExpression node) {
    }

    public void visitCreateDurationExpression(final CreateDurationExpression node) {
    }

    public void visitCreateExpression(final CreateExpression node) {
    }

    public void visitDelayStatement(final DelayStatement node) {
    }

    public void visitDeleteStatement(final DeleteStatement node) {
    }

    public void visitDeltaConstraint(final DeltaConstraint node) {
    }

    public void visitDictionaryAccessExpression(final DictionaryAccessExpression node) {
    }

    public void visitDictionaryContainsExpression(final DictionaryContainsExpression node) {
    }

    public void visitDictionaryKeysExpression(final DictionaryKeysExpression node) {
    }

    public void visitDictionaryType(final DictionaryType node) {
    }

    public void visitDictionaryValuesExpression(final DictionaryValuesExpression node) {
    }

    public void visitDigitsConstraint(final DigitsConstraint node) {
    }

    public void visitDomain(final Domain node) {
    }

    public void visitDomainFunctionInvocation(final DomainFunctionInvocation node) {
    }

    public void visitDomainService(final DomainService node) {
    }

    public void visitDomainServiceInvocation(final DomainServiceInvocation node) {
    }

    public void visitDomainTerminator(final DomainTerminator node) {
    }

    public void visitDomainTerminatorService(final DomainTerminatorService node) {
    }

    public void visitDurationLiteral(final DurationLiteral node) {
    }

    public void visitElementsExpression(final ElementsExpression node) {
    }

    public void visitEndlLiteral(final EndlLiteral node) {
    }

    public void visitEnumerateItem(final EnumerateItem node) {
    }

    public void visitEnumerateLiteral(final EnumerateLiteral node) {
    }

    public void visitEnumerateType(final EnumerateType node) {
    }

    public void visitEofExpression(final EofExpression node) {
    }

    public void visitEraseStatement(final EraseStatement node) {
    }

    public void visitEventDeclaration(final EventDeclaration node) {
    }

    public void visitEventExpression(final EventExpression node) {
    }

    public void visitExceptionDeclaration(final ExceptionDeclaration node) {
    }

    public void visitExceptionHandler(final ExceptionHandler node) {
    }

    public void visitExceptionReference(final ExceptionReference node) {
    }

    public void visitExitStatement(final ExitStatement node) {
    }

    public void visitFindAttributeNameExpression(final FindAttributeNameExpression node) {
    }

    public void visitFindExpression(final FindExpression node) {
    }

    public void visitFindParameterExpression(final FindParameterExpression node) {
    }

    public void visitFlushLiteral(final FlushLiteral node) {
    }

    public void visitForStatement(final ForStatement node) {
    }

    public void visitGenerateStatement(final GenerateStatement node) {
    }

    public void visitIdentifierDeclaration(final IdentifierDeclaration node) {
    }

    public void visitIfBranch(final IfStatement.Branch node) {
    }

    public void visitIfStatement(final IfStatement node) {
    }

    public void visitIndexedNameExpression(final IndexedNameExpression node) {
    }

    public void visitInstanceFunctionInvocation(final InstanceFunctionInvocation node) {
    }

    public void visitInstanceOrderingExpression(final InstanceOrderingExpression node) {
    }

    public void visitInstanceServiceInvocation(final InstanceServiceInvocation node) {
    }

    public void visitInstanceType(final InstanceType node) {
    }

    public void visitIntegerLiteral(final IntegerLiteral node) {
    }

    public void visitIOStreamStatement(final IOStreamStatement node) {
    }

    public void visitLinkUnlinkExpression(final LinkUnlinkExpression node) {
    }

    public void visitLinkUnlinkStatement(final LinkUnlinkStatement node) {
    }

    public void visitLoopFromToRange(final LoopSpec.FromToRange node) {
    }

    public void visitLoopTypeRange(final LoopSpec.TypeRange node) {
    }

    public void visitLoopVariableElements(final LoopSpec.VariableElements node) {
    }

    public void visitLoopVariableRange(final LoopSpec.VariableRange node) {
    }

    public void visitMinMaxRange(final MinMaxRange node) {
    }

    public void visitNavigationExpression(final NavigationExpression node) {
    }

    public void visitNullLiteral(final NullLiteral node) {
    }

    public void visitObjectDeclaration(final ObjectDeclaration node) {
    }

    public void visitObjectFunctionInvocation(final ObjectFunctionInvocation node) {
    }

    public void visitObjectNameExpression(final ObjectNameExpression node) {
    }

    public void visitObjectService(final ObjectService node) {
    }

    public void visitObjectServiceInvocation(final ObjectServiceInvocation node) {
    }

    public void visitParameterDefinition(final ParameterDefinition node) {
    }

    public void visitParameterNameExpression(final ParameterNameExpression node) {
    }

    public void visitParseExpression(final ParseExpression node) {
    }

    public void visitPragmaDefinition(final PragmaDefinition node) {
    }

    public void visitPragmaList(final PragmaList node) {
    }

    public void visitPragmaStatement(final PragmaStatement node) {
    }

    public void visitRaiseStatement(final RaiseStatement node) {
    }

    public void visitRangeConstraint(final RangeConstraint node) {
    }

    public void visitRealLiteral(final RealLiteral node) {
    }

    public void visitReferentialAttributeDefinition(final ReferentialAttributeDefinition node) {
    }

    public void visitReturnStatement(final ReturnStatement node) {
    }

    public void visitScheduleStatement(final ScheduleStatement node) {
    }

    public void visitSelectedAttributeExpression(final SelectedAttributeExpression node) {
    }

    public void visitSelectedComponentExpression(final SelectedComponentExpression node) {
    }

    public void visitSequenceType(final SequenceType node) {
    }

    public void visitSetType(final SetType node) {
    }

    public void visitSliceExpression(final SliceExpression node) {
    }

    public void visitSplitExpression(final SplitExpression node) {
    }

    public void visitState(final State node) {
    }

    public void visitStringLiteral(final StringLiteral node) {
    }

    public void visitStructureAggregate(final StructureAggregate node) {
    }

    public void visitStructureElement(final StructureElement node) {
    }

    public void visitStructureOrderingExpression(final StructureOrderingExpression node) {
    }

    public void visitStructureType(final StructureType node) {
    }

    public void visitTerminatorFunctionInvocation(final TerminatorFunctionInvocation node) {
    }

    public void visitTerminatorNameExpression(final TerminatorNameExpression node) {
    }

    public void visitTerminatorServiceInvocation(final TerminatorServiceInvocation node) {
    }

    public void visitThisLiteral(final ThisLiteral node) {
    }

    public void visitTimeFieldExpression(final TimeFieldExpression node) {
    }

    public void visitTimerFieldExpression(final TimerFieldExpression node) {
    }

    public void visitTimestampDeltaExpression(final TimestampDeltaExpression node) {
    }

    public void visitTimestampLiteral(final TimestampLiteral node) {
    }

    public void visitTransitionOption(final TransitionOption node) {
    }

    public void visitTransitionRow(final TransitionRow node) {
    }

    public void visitTransitionTable(final TransitionTable node) {
    }

    public void visitTypeDeclaration(final TypeDeclaration node) {
    }

    public void visitTypeNameExpression(final TypeNameExpression node) {
    }

    public void visitUnaryExpression(final UnaryExpression node) {
    }

    public void visitUnconstrainedArraySubtype(final UnconstrainedArraySubtype node) {
    }

    public void visitUnconstrainedArrayType(final UnconstrainedArrayType node) {
    }

    public void visitUserDefinedType(final UserDefinedType node) {
    }

    public void visitVariableDefinition(final VariableDefinition node) {
    }

    public void visitVariableNameExpression(final VariableNameExpression node) {
    }

    public void visitWhileStatement(final WhileStatement node) {
    }

    public void vistAssociativeRelationshipDeclaration(final AssociativeRelationshipDeclaration node) {
    }

    public void vistNormalRelationshipDeclaration(final NormalRelationshipDeclaration node) {
    }

    public void vistRelationshipSpecification(final RelationshipSpecification node) {
    }

    public void vistSubtypeRelationshipDeclaration(final SubtypeRelationshipDeclaration node) {
    }

    public void visitCreateAttributeValue(final CreateExpression.AttributeValue node) {
    }

    public void visitProject(final Project node) {
    }

    public void visitProjectDomain(final ProjectDomain node) {
    }

    public void visitProjectTerminator(final ProjectTerminator node) {
    }

    public void visitProjectTerminatorService(final ProjectTerminatorService node) {
    }

    public void visitStructureOrderingComponent(final Component node) {
    }

}
