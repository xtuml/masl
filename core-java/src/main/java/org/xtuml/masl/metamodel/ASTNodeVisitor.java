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
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.*;
import org.xtuml.masl.metamodel.type.*;

public interface ASTNodeVisitor<R, P> {

    R visit(ASTNode node, P p) throws Exception;

    R visitAnonymousStructure(AnonymousStructure node, P p) throws Exception;

    R visitAnyExpression(AnyExpression node, P p) throws Exception;

    R visitArrayType(ArrayType node, P p) throws Exception;

    R visitAssignmentStatement(AssignmentStatement node, P p) throws Exception;

    R visitAttributeDeclaration(AttributeDeclaration node, P p) throws Exception;

    R visitBagType(BagType node, P p) throws Exception;

    R visitBinaryExpression(BinaryExpression node, P p) throws Exception;

    R visitBooleanLiteral(BooleanLiteral node, P p) throws Exception;

    R visitBuiltinException(BuiltinException node, P p) throws Exception;

    R visitBuiltinType(BuiltinType node, P p) throws Exception;

    R visitCancelTimerStatement(CancelTimerStatement node, P p) throws Exception;

    R visitCaseAlternative(CaseStatement.Alternative node, P p) throws Exception;

    R visitCaseStatement(CaseStatement node, P p) throws Exception;

    R visitCastExpression(CastExpression node, P p) throws Exception;

    R visitCharacteristicExpression(CharacteristicExpression node, P p) throws Exception;

    R visitCharacteristicRange(CharacteristicRange node, P p) throws Exception;

    R visitCharacterLiteral(CharacterLiteral node, P p) throws Exception;

    R visitCodeBlock(CodeBlock node, P p) throws Exception;

    R visitConsoleLiteral(ConsoleLiteral node, P p) throws Exception;

    R visitConstrainedType(ConstrainedType node, P p) throws Exception;

    R visitCorrelatedNavExpression(CorrelatedNavExpression node, P p) throws Exception;

    R visitCreateDurationExpression(CreateDurationExpression node, P p) throws Exception;

    R visitCreateExpression(CreateExpression node, P p) throws Exception;

    R visitDelayStatement(DelayStatement node, P p) throws Exception;

    R visitDeleteStatement(DeleteStatement node, P p) throws Exception;

    R visitDeltaConstraint(DeltaConstraint node, P p) throws Exception;

    R visitDictionaryAccessExpression(DictionaryAccessExpression node, P p) throws Exception;

    R visitDictionaryContainsExpression(DictionaryContainsExpression node, P p) throws Exception;

    R visitDictionaryKeysExpression(DictionaryKeysExpression node, P p) throws Exception;

    R visitDictionaryType(DictionaryType node, P p) throws Exception;

    R visitDictionaryValuesExpression(DictionaryValuesExpression node, P p) throws Exception;

    R visitDigitsConstraint(DigitsConstraint node, P p) throws Exception;

    R visitDomain(Domain node, P p) throws Exception;

    R visitDomainFunctionInvocation(DomainFunctionInvocation node, P p) throws Exception;

    R visitDomainService(DomainService node, P p) throws Exception;

    R visitDomainServiceInvocation(DomainServiceInvocation node, P p) throws Exception;

    R visitDomainTerminator(DomainTerminator node, P p) throws Exception;

    R visitDomainTerminatorService(DomainTerminatorService node, P p) throws Exception;

    R visitDurationLiteral(DurationLiteral node, P p) throws Exception;

    R visitElementsExpression(ElementsExpression node, P p) throws Exception;

    R visitEndlLiteral(EndlLiteral node, P p) throws Exception;

    R visitEnumerateItem(EnumerateItem node, P p) throws Exception;

    R visitEnumerateLiteral(EnumerateLiteral node, P p) throws Exception;

    R visitEnumerateType(EnumerateType node, P p) throws Exception;

    R visitEofExpression(EofExpression node, P p) throws Exception;

    R visitEraseStatement(EraseStatement node, P p) throws Exception;

    R visitEventDeclaration(EventDeclaration node, P p) throws Exception;

    R visitEventExpression(EventExpression node, P p) throws Exception;

    R visitExceptionDeclaration(ExceptionDeclaration node, P p) throws Exception;

    R visitExceptionHandler(ExceptionHandler node, P p) throws Exception;

    R visitExceptionReference(ExceptionReference node, P p) throws Exception;

    R visitExitStatement(ExitStatement node, P p) throws Exception;

    R visitFindAttributeNameExpression(FindAttributeNameExpression node, P p) throws Exception;

    R visitFindExpression(FindExpression node, P p) throws Exception;

    R visitFindParameterExpression(FindParameterExpression node, P p) throws Exception;

    R visitFlushLiteral(FlushLiteral node, P p) throws Exception;

    R visitForStatement(ForStatement node, P p) throws Exception;

    R visitGenerateStatement(GenerateStatement node, P p) throws Exception;

    R visitIdentifierDeclaration(IdentifierDeclaration node, P p) throws Exception;

    R visitIfBranch(IfStatement.Branch node, P p) throws Exception;

    R visitIfStatement(IfStatement node, P p) throws Exception;

    R visitIndexedNameExpression(IndexedNameExpression node, P p) throws Exception;

    R visitInstanceFunctionInvocation(InstanceFunctionInvocation node, P p) throws Exception;

    R visitInstanceOrderingExpression(InstanceOrderingExpression node, P p) throws Exception;

    R visitInstanceServiceInvocation(InstanceServiceInvocation node, P p) throws Exception;

    R visitInstanceType(InstanceType node, P p) throws Exception;

    R visitIntegerLiteral(IntegerLiteral node, P p) throws Exception;

    R visitIOStreamStatement(IOStreamStatement node, P p) throws Exception;

    R visitLinkUnlinkExpression(LinkUnlinkExpression node, P p) throws Exception;

    R visitLinkUnlinkStatement(LinkUnlinkStatement node, P p) throws Exception;

    R visitLoopFromToRange(LoopSpec.FromToRange node, P p) throws Exception;

    R visitLoopTypeRange(LoopSpec.TypeRange node, P p) throws Exception;

    R visitLoopVariableElements(LoopSpec.VariableElements node, P p) throws Exception;

    R visitLoopVariableRange(LoopSpec.VariableRange node, P p) throws Exception;

    R visitMinMaxRange(MinMaxRange node, P p) throws Exception;

    R visitNavigationExpression(NavigationExpression node, P p) throws Exception;

    R visitNull(P p) throws Exception;

    R visitNullLiteral(NullLiteral node, P p) throws Exception;

    R visitObjectDeclaration(ObjectDeclaration node, P p) throws Exception;

    R visitObjectFunctionInvocation(ObjectFunctionInvocation node, P p) throws Exception;

    R visitObjectNameExpression(ObjectNameExpression node, P p) throws Exception;

    R visitObjectService(ObjectService node, P p) throws Exception;

    R visitObjectServiceInvocation(ObjectServiceInvocation node, P p) throws Exception;

    R visitParameterDefinition(ParameterDefinition node, P p) throws Exception;

    R visitParameterNameExpression(ParameterNameExpression node, P p) throws Exception;

    R visitParseExpression(ParseExpression node, P p) throws Exception;

    R visitPragmaDefinition(PragmaDefinition node, P p) throws Exception;

    R visitPragmaList(PragmaList node, P p) throws Exception;

    R visitPragmaStatement(PragmaStatement node, P p) throws Exception;

    R visitRaiseStatement(RaiseStatement node, P p) throws Exception;

    R visitRangeConstraint(RangeConstraint node, P p) throws Exception;

    R visitRealLiteral(RealLiteral node, P p) throws Exception;

    R visitReferentialAttributeDefinition(ReferentialAttributeDefinition node, P p) throws Exception;

    R visitReturnStatement(ReturnStatement node, P p) throws Exception;

    R visitScheduleStatement(ScheduleStatement node, P p) throws Exception;

    R visitSelectedAttributeExpression(SelectedAttributeExpression node, P p) throws Exception;

    R visitSelectedComponentExpression(SelectedComponentExpression node, P p) throws Exception;

    R visitSequenceType(SequenceType node, P p) throws Exception;

    R visitSetType(SetType node, P p) throws Exception;

    R visitSliceExpression(SliceExpression node, P p) throws Exception;

    R visitSplitExpression(SplitExpression node, P p) throws Exception;

    R visitState(State node, P p) throws Exception;

    R visitStringLiteral(StringLiteral node, P p) throws Exception;

    R visitStructureAggregate(StructureAggregate node, P p) throws Exception;

    R visitStructureElement(StructureElement node, P p) throws Exception;

    R visitStructureOrderingExpression(StructureOrderingExpression node, P p) throws Exception;

    R visitStructureType(StructureType node, P p) throws Exception;

    R visitTerminatorFunctionInvocation(TerminatorFunctionInvocation node, P p) throws Exception;

    R visitTerminatorNameExpression(TerminatorNameExpression node, P p) throws Exception;

    R visitTerminatorServiceInvocation(TerminatorServiceInvocation node, P p) throws Exception;

    R visitThisLiteral(ThisLiteral node, P p) throws Exception;

    R visitTimeFieldExpression(TimeFieldExpression node, P p) throws Exception;

    R visitTimerFieldExpression(TimerFieldExpression node, P p) throws Exception;

    R visitTimestampDeltaExpression(TimestampDeltaExpression node, P p) throws Exception;

    R visitTimestampLiteral(TimestampLiteral node, P p) throws Exception;

    R visitTransitionOption(TransitionOption node, P p) throws Exception;

    R visitTransitionRow(TransitionRow node, P p) throws Exception;

    R visitTransitionTable(TransitionTable node, P p) throws Exception;

    R visitTypeDeclaration(TypeDeclaration node, P p) throws Exception;

    R visitTypeNameExpression(TypeNameExpression node, P p) throws Exception;

    R visitUnaryExpression(UnaryExpression node, P p) throws Exception;

    R visitUnconstrainedArraySubtype(UnconstrainedArraySubtype node, P p) throws Exception;

    R visitUnconstrainedArrayType(UnconstrainedArrayType node, P p) throws Exception;

    R visitUserDefinedType(UserDefinedType node, P p) throws Exception;

    R visitVariableDefinition(VariableDefinition node, P p) throws Exception;

    R visitVariableNameExpression(VariableNameExpression node, P p) throws Exception;

    R visitWhileStatement(WhileStatement node, P p) throws Exception;

    R vistAssociativeRelationshipDeclaration(AssociativeRelationshipDeclaration node, P p) throws Exception;

    R vistNormalRelationshipDeclaration(NormalRelationshipDeclaration node, P p) throws Exception;

    R vistRelationshipSpecification(RelationshipSpecification node, P p) throws Exception;

    R vistSubtypeRelationshipDeclaration(SubtypeRelationshipDeclaration node, P p) throws Exception;

    R visitCreateAttributeValue(CreateExpression.AttributeValue node, P p) throws Exception;

}
