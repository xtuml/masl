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
package org.xtuml.masl.metamodelImpl.error;

import org.xtuml.masl.error.ErrorType;

import java.util.prefs.Preferences;

public enum SemanticErrorCode implements org.xtuml.masl.error.ErrorCode {
    DomainAlreadyDefined("redefinition of domain ''{0}''"),

    AttributeAlreadyDefinedOnObject("attribute ''{0}'' already defined on object at {1}"), EventAlreadyDefinedOnObject(
            "attribute ''{0}'' already defined on object at {1}"), StateAlreadyDefinedOnObject(
            "state ''{0}'' already defined on object at {1}"), ServiceAlreadyDefinedOnObject(
            "service ''{0}'' already defined on object at {1}"), ServiceAlreadyDefinedOnTerminator(
            "service ''{0}'' already defined on terminator at {1}"), NameAlreadyDefinedOnObject(
            "name ''{0}'' already defined on object 'at {1}"),

    ServiceAlreadyDefinedInDomain("service ''{0}'' already defined in domain at {1}"), TypeAlreadyDefinedInDomain(
            "type ''{0}'' already defined in domain at {1}"), ExceptionAlreadyDefinedInDomain(
            "exception ''{0}'' already defined in domain at {1}"), RelationshipAlreadyDefinedInDomain(
            "relationship ''{0}'' already defined in domain at {1}"), ObjectAlreadyDefinedInDomain(
            "object ''{0}'' already defined in domain at {1}"), TerminatorAlreadyDefinedInDomain(
            "terminator ''{0}'' already defined in domain at {1}"), NameAlreadyDefinedInDomain(
            "name ''{0}'' already defined in domain at {1}"),

    ParameterAlreadyDefinedOnService("parameter ''{0}'' already defined on service at {1}"), ParameterAlreadyDefinedOnState(
            "parameter ''{0}'' already defined on state at {1}"), ParameterAlreadyDefinedOnEvent(
            "parameter ''{0}'' already defined on event at {1}"),

    ElementAlreadyDefinedOnStructure("element ''{0}'' already defined on structure at {1}"), EnumerateAlreadyDefinedOnEnumeration(
            "enumerate ''{0}'' already defined on enumeration at {1}"),

    NameRedefinition("redefinition of ''{0}'' previously defined at {1}"),

    DomainNotFound("domain ''{0}'' not found"), TypeNotFound("type ''{0}'' not found"), ExceptionNotFound(
            "exception ''{0}'' not found"),

    NameNotFoundInScope("Name ''{0}'' not found in current scope"),

    ServiceNotFoundInDomain("service ''{0}'' not found in domain ''{1}''"), TypeNotFoundInDomain(
            "type ''{0}'' not found in domain ''{1}''"), RelationshipNotFoundInDomain(
            "relationship ''{0}'' not found in domain ''{1}''"), ExceptionNotFoundInDomain(
            "exception ''{0}'' not found in domain ''{1}''"), ObjectNotFoundInDomain(
            "object ''{0}'' not found in domain ''{1}''"), TerminatorNotFoundInDomain(
            "object ''{0}'' not found in domain ''{1}''"), NameNotFoundInDomain(
            "name ''{0}'' not found in domain ''{1}''"),

    AttributeNotFoundOnObject("attribute ''{0}'' not found on object ''{1}''"), StateNotFoundOnObject(
            "state ''{0}'' not found on object ''{1}''"), EventNotFoundOnObject(
            "event ''{0}'' not found on object ''{1}''"), ServiceNotFoundOnObject(
            "service ''{0}'' not found on object ''{1}''"), ServiceNotFoundOnTerminator(
            "service ''{0}'' not found on terminator ''{1}''"),

    MatchingServiceNotInDomain("service ''{0}'' not found in domain ''{1}'' with required signature"), MatchingServiceNotFoundOnObject(
            "service ''{0}'' not found on object ''{1}'' with required signature"), MatchingServiceNotFoundOnTerminator(
            "service ''{0}'' not found on terminator ''{1}'' with required signature"),

    ParameterNotFoundOnService("parameter ''{0}'' not found on service ''{1}''"), ParameterNotFoundOnState(
            "parameter ''{0}'' not found on state ''{1}''"), ParameterNotFoundOnEvent(
            "parameter ''{0}'' not found on event ''{1}''"),

    ElementNotFoundOnStructure("element ''{0}'' not found on structure ''{1}''"), EnumerateNotFoundOnEnumeration(
            "enumerate ''{0}'' not found on enumeration ''{1}''"),

    RefAttNotIdentifier("formalising attribute ''{0}'' must be an identifier of object ''{1}''"), RefAttNotSameType(
            "formalising attribute ''{0}'' on object ''{1}'' must be the same type as its referential attribute"), RefAttWrongEnd(
            "Relationship ''{0}'' must be formalised on on object ''{1}''"), RefAttBothEnds(
            "Relationship ''{0}'' cannot be formalised on on both object ''{1}'' and ''{2}''"),

    NoConversion("no conversion possible from ''{0}'' to ''{1}''"), CastRequired(
            "cast required for conversion from ''{0}'' to ''{1}''"),

    AmbiguousFunctionCall("call of overloaded function is ambiguous. Arguments {0} match: {1}"), NoMatchingFunctionCall(
            "no matching function call. Arguments {0} do not match: {1}"), NoFunctionCall("no matching function"), AmbiguousServiceCall(
            "call of overloaded service is ambiguous. Arguments {0} match: {1}"), NoMatchingServiceCall(
            "no matching service call. Arguments {0} do not match: {1}"), NoServiceCall("no matching service"), CallNotValid(
            "no invocation possible"), NotInstanceType("expression must be an instance type. Found ''{0}''"), NotInstanceOrCollType(
            "expression must be an instance or collection of instances type. Found ''{0}''"),

    NoLink("relationship ''{0}'' does not link to object ''{1}''"), NoRelationshipMatch(
            "no relationship matches ''{0}'' - assuming ''{1}''"), NoRelationshipMatchToAssoc(
            "relationship ''{0}'' must be to associative object for correlated navigate"), AmbiguousRelationship(
            "ambiguous relationship ''{0}'' - assuming ''{1}''"), CorrelateNotAssociative(
            "relationship ''{0}'' must be associative for correlated navigate"), CorrelateObjsIncorrect(
            "no correlation between ''{0}'' and ''{1}'' along ''{2}''"),

    NotObjectMember("''{0}'' not found on object ''{1}''"), OnlyForInstance(
            "''{0}'' is only valid for instance of ''{1}''"), OnlyForObject(
            "''{0}'' is not valid for instance of ''{1}''. Assuming {1}.{0}"), NotEnumerate(
            "''{0}'' is not an enumerate"), DotNotValid("''.'' not valid for ''{1}''"), TerminatorMemberNotValid(
            "''~>'' not valid for ''{1}''"), OrderNotCollection(
            "order expression must be a collection of structures or instances."),

    InterfaceNotFound("interface ''{0}'' not found."), ModelNotFound("model ''{0}'' not found."), EmptyIdentifier(
            "identifier has no attributes"), NotAssignable("expression of type ''{0}'' is not assignable to ''{1}''"), NotWriteable(
            "expression is not modifiable"), AssignToReadOnly(
            "variable ''{0}'' is not modifiable as it is declared readonly"), AssignToIdentifier(
            "attribute ''{0}'' is not modifiable as it forms part of an identifier"), AssignToReferential(
            "attribute ''{0}'' is not modifiable as it is referential"), AssignToUnique(
            "attribute ''{0}'' is not modifiable as it is marked as unique"), CannotWriteToAttributeType(
            "attribute ''{0}'' is not modifiable as it is of type ''{1}''"), AssignToInParameter(
            "input parameter is not modifiable"), NotUsableInCase(
            "expression of type ''{0}'' is usable to discriminate case of type ''{1}''"),

    OnlyDeferToSubtype("service can only be deferred to a subtype"),

    TransitionOptionExists("event ''{0}'' already handled by ''{2}'' at {1}"), CreationEventFromState(
            "creation event must not transition from existing state"), AssignerEventNotFromAssigner(
            "assigner event must transition from assigner state"), NormalEventNotFromNormalState(
            "event must transition from existing state"), NoRowForState("no transitions defined from state ''{0}''"),

    OnlyOneStateMachine("state machine already defined on object ''{0}''"), OnlyOneAssignerStateMachine(
            "assigner state machine already defined on object ''{0}''"),

    FindOnlyOnInstanceCollection("find must be on a collection of instances or an object population"),

    CharacterLiteralInvalidLength("character literal must contain exactly one character or escape sequence"), InvalidEscapeSequence(
            "invalid escape sequence"), DelayParameterNotDuration("delay parameter must be a duration"), ExpectedBooleanCondition(
            "expected boolean condition. Found ''{0}''"), ExpectedNumericExpression(
            "expected numeric expression. Found ''{0}''"), ExpectedDictionaryExpression(
            "expected dictionary expression. Found ''{0}''"), InvalidLoopSpec(
            "for loop requires collection or range. Found ''{0}''"),

    NoDestinationInstance("destination instance must be supplied for event ''{0}''"), IgnoringCreationDestinationInstance(
            "destination instance not required for creation event"), IgnoringAssignerDestinationInstance(
            "destination instance not required for assigner event"), InvalidEventDestination(
            "an instance of ''{0}'' cannot receive this event"), EventDestinationNotInstance(
            "events can only be generated to instances. Found ''{0}''"), NumberEventArgsIncorrect(
            "expected {0} arguments to event. Found {1}"), ExpectedDeviceForStream(
            "expected device for streaming operator. Found ''{0}''"), ExpectedInstanceOfExpression(
            "expected instance of ''{0}''. Found ''{1}''"), AssocNeedsUsing(
            "link or unlink of an associative relationship requires a using clause, or use a link/unlink expression"), AssociativeRelationshipRequired(
            "link or unlink expression requires an associative relationship"), CannotDeduceAssocIdentifier(
            "cannot deduce identifier attribute ''{1}'' of associative object ''{0}''. Must use link statement specifying explicit associative"), CannotDeduceCurrentState(
            "cannot deduce current state of associative object ''{0}''. Must use link statement specifying explicit associative"), LinkMustSupplyRhs(
            "link must specify instance to link to"), NoLinkToAssoc(
            "cannot link to associative object - use ''using'' clause"), NoLinkFromAssoc(
            "cannot link from associative object - use ''using'' clause"), NoCollectionAllowedOnLink(
            "must supply single instance for relationship with this cardinality"), NonAssocWithUsing(
            "using clause not valid with non-associative relationship"), ReturnNotInFunction(
            "return statement only valid within function definition"), OperatorOnlyOnNumeric(
            "operator ''{0}'' requires numeric operand. Found ''{1}''"), OperatorOperandsNotCompatible(
            "operands of type ''{0}'' and ''{1}'' are not compatible for operator ''{2}''"), ComparisonNotValidForType(
            "operand of type ''{0}'' not valid for operator ''{1}''"), AttributesAreOpaque(
            "complex object attributes are opaque"), CastTakesOneParam("cast must take a single parameter"), ElementNotFoundOnType(
            "element ''{0}'' not found on type ''{1}'' as it is not a structure"), ThisNotValid(
            "''this'' expression only valid in instance service or instance state"), NoOverloadOfInstanceAndObjectService(
            "cannot mix overloads of instance and object services"), NoPreferredIdentifier(
            "no preferred identifier defined for object ''{0}''"), RelationshipFormalismMissing(
            "object ''{0}'' must formalise relationship ''{1}''"), RelationshipFormalismIncorrect(
            "object ''{0}'' does not formalise relationship ''{1}'' correctly"), CreateIdentifierMissing(
            "create must specify value for identifier attribute ''{0}.{1}''"), CreateInitialStateMissing(
            "create of ''{0}'' must specify an initial state"), ArrayBoundsNotRange("array bounds must be a range"), ArrayBoundsNotConstant(
            "array bounds must be constant"), CastOnlyInExpression("cast is only valid in an expression"), NoObjectForEvent(
            "no object in scope for event ''{0}''"), TimestampFormatNotRecognised(
            "timestamp literal is not in ISO8610 date or date-time format"), TimestampFieldOutOfRange(
            "timestamp field ''{0}'' out of range"), DurationFormatNotRecognised(
            "duration literal is not in ISO 8610 duration format"), IndeterminateDuration(
            "duration literal is indeterminate as it specified years or months"), CharacteristicSuffixInvalid(
            "characteristic suffix ''{0}'' is invalid for ''{1}''"), CharacteristicNotFound(
            "characteristic ''{0}'' not found"), CharacteristicNotValid("characteristic ''{0}'' not valid on ''{1}''"), CharacteristicNotValidParam(
            "characteristic ''{0}'' with {2,choice,0#no parameters|1#1 parameter|1<{2} parameters} not valid on ''{1}''"), CharacteristicRequiresType(
            "characteristic ''{0}'' only valid on a type name"), CharacteristicArgumentCountMismatch(
            "characteristic ''{0}'' requires {1} arguments. Found {2}"), ExpectedCollectionOrString(
            "elements characteristic requires collection or string. Found ''{0}''"), AnonymousUserDefinedType(
            "user defined type cannot be anonymous"), ExpectedDeviceExpression(
            "expected device expression. Found ''{0}''"), FunctionMayNotReturnAValue(
            "function does not return a value for all branches"), IndexNotValid(
            "expected sequence, array or string for slice or index expression. Found ''{0}''"), EraseOnlyValidforDictionary(
            "erase must operate on dictionary key"), PrivateTypeCannotBeUsedPublicly(
            "private type ''{0}'' cannot be used in a public context"), RecursiveStructure(
            "recursive structure element ''{0}.{1}''"), TypeVisibility(
            "type ''{0}'' visibility does not match forward declaration"), TypeNotDefined(
            "type ''{0}'' was declared but not defined"),
    ;

    private static final String FORMAT = "format";

    private static final String TYPE = "type";

    SemanticErrorCode(final ErrorType defaultErrorType, final String defaultMessageFormat) {
        this.defaultMessageFormat = defaultMessageFormat;
        this.defaultErrorType = defaultErrorType;
    }

    SemanticErrorCode(final String defaultMessageFormat) {
        this(ErrorType.Error, defaultMessageFormat);
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.valueOf(getPreference(TYPE, getDefaultErrorType().toString()));
    }

    String getMessageFormat() {
        return getPreference(FORMAT, getDefaultMessageFormat());
    }

    String getPreference(final String name, final String defaultValue) {
        final String
                result =
                Preferences.userRoot().node("/masl/errorcodes").get(this + "-" + name,
                                                                    Preferences.systemRoot().node("/masl/errorcodes").get(
                                                                            this + "-" + name,
                                                                            defaultValue));
        return result;
    }

    private ErrorType getDefaultErrorType() {
        return defaultErrorType;
    }

    private String getDefaultMessageFormat() {
        return defaultMessageFormat;
    }

    private final ErrorType defaultErrorType;
    private final String defaultMessageFormat;

}
