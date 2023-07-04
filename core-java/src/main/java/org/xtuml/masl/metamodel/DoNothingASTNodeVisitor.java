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

public class DoNothingASTNodeVisitor<R, P> extends AbstractASTNodeVisitor<R, P> {

    public DoNothingASTNodeVisitor() {
    }

    @Override
    public R visitAnonymousStructure(final AnonymousStructure node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitAnyExpression(final AnyExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitArrayType(final ArrayType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitAssignmentStatement(final AssignmentStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitAttributeDeclaration(final AttributeDeclaration node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitBagType(final BagType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitBinaryExpression(final BinaryExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitBooleanLiteral(final BooleanLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitBuiltinException(final BuiltinException node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitBuiltinType(final BuiltinType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCancelTimerStatement(final CancelTimerStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCaseAlternative(final Alternative node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCaseStatement(final CaseStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCastExpression(final CastExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCharacterLiteral(final CharacterLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCharacteristicExpression(final CharacteristicExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCharacteristicRange(final CharacteristicRange node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCodeBlock(final CodeBlock node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitConsoleLiteral(final ConsoleLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitConstrainedType(final ConstrainedType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCorrelatedNavExpression(final CorrelatedNavExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCreateAttributeValue(final AttributeValue node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCreateDurationExpression(final CreateDurationExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitCreateExpression(final CreateExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDelayStatement(final DelayStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDeleteStatement(final DeleteStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDeltaConstraint(final DeltaConstraint node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDictionaryAccessExpression(final DictionaryAccessExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDictionaryContainsExpression(final DictionaryContainsExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDictionaryKeysExpression(final DictionaryKeysExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDictionaryType(final DictionaryType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDictionaryValuesExpression(final DictionaryValuesExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDigitsConstraint(final DigitsConstraint node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDomain(final Domain node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDomainFunctionInvocation(final DomainFunctionInvocation node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDomainService(final DomainService node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDomainServiceInvocation(final DomainServiceInvocation node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDomainTerminator(final DomainTerminator node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDomainTerminatorService(final DomainTerminatorService node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitDurationLiteral(final DurationLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitElementsExpression(final ElementsExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitEndlLiteral(final EndlLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitEnumerateItem(final EnumerateItem node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitEnumerateLiteral(final EnumerateLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitEnumerateType(final EnumerateType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitEofExpression(final EofExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitEraseStatement(final EraseStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitEventDeclaration(final EventDeclaration node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitEventExpression(final EventExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitExceptionDeclaration(final ExceptionDeclaration node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitExceptionHandler(final ExceptionHandler node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitExceptionReference(final ExceptionReference node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitExitStatement(final ExitStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitFindAttributeNameExpression(final FindAttributeNameExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitFindExpression(final FindExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitFindParameterExpression(final FindParameterExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitFlushLiteral(final FlushLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitForStatement(final ForStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitGenerateStatement(final GenerateStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitIOStreamStatement(final IOStreamStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitIdentifierDeclaration(final IdentifierDeclaration node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitIfBranch(final Branch node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitIfStatement(final IfStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitIndexedNameExpression(final IndexedNameExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitInstanceFunctionInvocation(final InstanceFunctionInvocation node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitInstanceOrderingExpression(final InstanceOrderingExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitInstanceServiceInvocation(final InstanceServiceInvocation node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitInstanceType(final InstanceType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitIntegerLiteral(final IntegerLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitLinkUnlinkExpression(final LinkUnlinkExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitLinkUnlinkStatement(final LinkUnlinkStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitLoopFromToRange(final FromToRange node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitLoopTypeRange(final TypeRange node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitLoopVariableElements(final VariableElements node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitLoopVariableRange(final VariableRange node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitMinMaxRange(final MinMaxRange node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitNavigationExpression(final NavigationExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitNullLiteral(final NullLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitObjectDeclaration(final ObjectDeclaration node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitObjectFunctionInvocation(final ObjectFunctionInvocation node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitObjectNameExpression(final ObjectNameExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitObjectService(final ObjectService node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitObjectServiceInvocation(final ObjectServiceInvocation node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitParameterDefinition(final ParameterDefinition node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitParameterNameExpression(final ParameterNameExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitParseExpression(final ParseExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitPragmaDefinition(final PragmaDefinition node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitPragmaList(final PragmaList node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitPragmaStatement(final PragmaStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitRaiseStatement(final RaiseStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitRangeConstraint(final RangeConstraint node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitRealLiteral(final RealLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitReferentialAttributeDefinition(final ReferentialAttributeDefinition node, final P p) throws
                                                                                                       Exception {
        return null;
    }

    @Override
    public R visitReturnStatement(final ReturnStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitScheduleStatement(final ScheduleStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitSelectedAttributeExpression(final SelectedAttributeExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitSelectedComponentExpression(final SelectedComponentExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitSequenceType(final SequenceType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitSetType(final SetType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitSliceExpression(final SliceExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitSplitExpression(final SplitExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitState(final State node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitStringLiteral(final StringLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitStructureAggregate(final StructureAggregate node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitStructureElement(final StructureElement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitStructureOrderingExpression(final StructureOrderingExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitStructureType(final StructureType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTerminatorFunctionInvocation(final TerminatorFunctionInvocation node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTerminatorNameExpression(final TerminatorNameExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTerminatorServiceInvocation(final TerminatorServiceInvocation node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitThisLiteral(final ThisLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTimeFieldExpression(final TimeFieldExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTimerFieldExpression(final TimerFieldExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTimestampDeltaExpression(final TimestampDeltaExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTimestampLiteral(final TimestampLiteral node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTransitionOption(final TransitionOption node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTransitionRow(final TransitionRow node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTransitionTable(final TransitionTable node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTypeDeclaration(final TypeDeclaration node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitTypeNameExpression(final TypeNameExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitUnaryExpression(final UnaryExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitUnconstrainedArraySubtype(final UnconstrainedArraySubtype node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitUnconstrainedArrayType(final UnconstrainedArrayType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitUserDefinedType(final UserDefinedType node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitVariableDefinition(final VariableDefinition node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitVariableNameExpression(final VariableNameExpression node, final P p) throws Exception {
        return null;
    }

    @Override
    public R visitWhileStatement(final WhileStatement node, final P p) throws Exception {
        return null;
    }

    @Override
    public R vistAssociativeRelationshipDeclaration(final AssociativeRelationshipDeclaration node, final P p) throws
                                                                                                              Exception {
        return null;
    }

    @Override
    public R vistNormalRelationshipDeclaration(final NormalRelationshipDeclaration node, final P p) throws Exception {
        return null;
    }

    @Override
    public R vistRelationshipSpecification(final RelationshipSpecification node, final P p) throws Exception {
        return null;
    }

    @Override
    public R vistSubtypeRelationshipDeclaration(final SubtypeRelationshipDeclaration node, final P p) throws Exception {
        return null;
    }

}
