//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
tree grammar Walker;

options
{
  tokenVocab=MaslP;
  ASTLabelType=CommonTree;
  superClass=MaslTreeParser;
}

scope NameScope
{
  NameLookup lookup;
}

scope WhereClauseScope
{
  ObjectDeclaration parentObject;
}

@annotations
{
@SuppressWarnings("all")
}

@header
{
package org.xtuml.masl.antlr;

import java.util.Collections;

import org.xtuml.masl.metamodelImpl.code.AssignmentStatement;
import org.xtuml.masl.metamodelImpl.code.CaseStatement;
import org.xtuml.masl.metamodelImpl.code.CodeBlock;
import org.xtuml.masl.metamodelImpl.code.DelayStatement;
import org.xtuml.masl.metamodelImpl.code.DeleteStatement;
import org.xtuml.masl.metamodelImpl.code.EraseStatement;
import org.xtuml.masl.metamodelImpl.code.ExceptionHandler;
import org.xtuml.masl.metamodelImpl.code.ExitStatement;
import org.xtuml.masl.metamodelImpl.code.ServiceInvocation;
import org.xtuml.masl.metamodelImpl.code.ForStatement;
import org.xtuml.masl.metamodelImpl.code.LoopSpec;
import org.xtuml.masl.metamodelImpl.code.GenerateStatement;
import org.xtuml.masl.metamodelImpl.code.IOStreamStatement;
import org.xtuml.masl.metamodelImpl.code.IfStatement;
import org.xtuml.masl.metamodelImpl.code.LinkUnlinkStatement;
import org.xtuml.masl.metamodelImpl.code.PragmaStatement;
import org.xtuml.masl.metamodelImpl.code.RaiseStatement;
import org.xtuml.masl.metamodelImpl.code.ReturnStatement;
import org.xtuml.masl.metamodelImpl.code.ScheduleStatement;
import org.xtuml.masl.metamodelImpl.code.CancelTimerStatement;
import org.xtuml.masl.metamodelImpl.code.Statement;
import org.xtuml.masl.metamodelImpl.code.VariableDefinition;
import org.xtuml.masl.metamodelImpl.code.WhileStatement;
import org.xtuml.masl.metamodelImpl.common.ParameterDefinition;
import org.xtuml.masl.metamodelImpl.common.ParameterModeType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaDefinition;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.common.Visibility;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.domain.DomainService;
import org.xtuml.masl.metamodelImpl.domain.Domains;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminator;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminatorService;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.exception.ExceptionDeclaration;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.expression.BinaryExpression;
import org.xtuml.masl.metamodelImpl.expression.BooleanLiteral;
import org.xtuml.masl.metamodelImpl.expression.CallExpression;
import org.xtuml.masl.metamodelImpl.expression.CharacterLiteral;
import org.xtuml.masl.metamodelImpl.expression.CharacteristicExpression;
import org.xtuml.masl.metamodelImpl.expression.ConsoleLiteral;
import org.xtuml.masl.metamodelImpl.expression.CorrelatedNavExpression;
import org.xtuml.masl.metamodelImpl.expression.CreateExpression;
import org.xtuml.masl.metamodelImpl.expression.DotExpression;
import org.xtuml.masl.metamodelImpl.expression.EndlLiteral;
import org.xtuml.masl.metamodelImpl.expression.EventExpression;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.FindAttributeNameExpression;
import org.xtuml.masl.metamodelImpl.expression.FindExpression;
import org.xtuml.masl.metamodelImpl.expression.FlushLiteral;
import org.xtuml.masl.metamodelImpl.expression.IntegerLiteral;
import org.xtuml.masl.metamodelImpl.expression.StringLiteral;
import org.xtuml.masl.metamodelImpl.expression.CharacterLiteral;
import org.xtuml.masl.metamodelImpl.expression.TimestampLiteral;
import org.xtuml.masl.metamodelImpl.expression.DurationLiteral;
import org.xtuml.masl.metamodelImpl.expression.MinMaxRange;
import org.xtuml.masl.metamodelImpl.expression.NavigationExpression;
import org.xtuml.masl.metamodelImpl.expression.LinkUnlinkExpression;
import org.xtuml.masl.metamodelImpl.expression.NullLiteral;
import org.xtuml.masl.metamodelImpl.expression.ObjectNameExpression;
import org.xtuml.masl.metamodelImpl.expression.OrderingExpression;
import org.xtuml.masl.metamodelImpl.expression.RealLiteral;
import org.xtuml.masl.metamodelImpl.expression.SliceExpression;
import org.xtuml.masl.metamodelImpl.expression.StringLiteral;
import org.xtuml.masl.metamodelImpl.expression.StructureAggregate;
import org.xtuml.masl.metamodelImpl.expression.ThisLiteral;
import org.xtuml.masl.metamodelImpl.expression.TypeNameExpression;
import org.xtuml.masl.metamodelImpl.expression.UnaryExpression;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.metamodelImpl.name.NameLookup;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.object.IdentifierDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectService;
import org.xtuml.masl.metamodelImpl.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodelImpl.project.Project;
import org.xtuml.masl.metamodelImpl.project.ProjectDomain;
import org.xtuml.masl.metamodelImpl.project.ProjectTerminator;
import org.xtuml.masl.metamodelImpl.project.ProjectTerminatorService;
import org.xtuml.masl.metamodelImpl.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.HalfRelationship;
import org.xtuml.masl.metamodelImpl.relationship.MultiplicityType;
import org.xtuml.masl.metamodelImpl.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodelImpl.statemodel.EventDeclaration;
import org.xtuml.masl.metamodelImpl.statemodel.EventType;
import org.xtuml.masl.metamodelImpl.statemodel.State;
import org.xtuml.masl.metamodelImpl.statemodel.StateType;
import org.xtuml.masl.metamodelImpl.statemodel.TransitionOption;
import org.xtuml.masl.metamodelImpl.statemodel.TransitionRow;
import org.xtuml.masl.metamodelImpl.statemodel.TransitionTable;
import org.xtuml.masl.metamodelImpl.statemodel.TransitionType;
import org.xtuml.masl.metamodelImpl.type.ArrayType;
import org.xtuml.masl.metamodelImpl.type.BagType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.BuiltinType;
import org.xtuml.masl.metamodelImpl.type.ConstrainedType;
import org.xtuml.masl.metamodelImpl.type.DeltaConstraint;
import org.xtuml.masl.metamodelImpl.type.DigitsConstraint;
import org.xtuml.masl.metamodelImpl.type.EnumerateItem;
import org.xtuml.masl.metamodelImpl.type.EnumerateType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;
import org.xtuml.masl.metamodelImpl.type.RangeConstraint;
import org.xtuml.masl.metamodelImpl.type.SequenceType;
import org.xtuml.masl.metamodelImpl.type.DictionaryType;
import org.xtuml.masl.metamodelImpl.type.SetType;
import org.xtuml.masl.metamodelImpl.type.StructureElement;
import org.xtuml.masl.metamodelImpl.type.StructureType;
import org.xtuml.masl.metamodelImpl.type.TypeConstraint;
import org.xtuml.masl.metamodelImpl.type.TypeDeclaration;
import org.xtuml.masl.metamodelImpl.type.TypeDefinition;
import org.xtuml.masl.metamodelImpl.type.UnconstrainedArraySubtype;
import org.xtuml.masl.metamodelImpl.type.UnconstrainedArrayType;
import org.xtuml.masl.metamodelImpl.type.UserDefinedType;
}

@members
{
  private Domain currentDomain;
  private Project currentProject;
  private ProjectDomain currentPrjDomain;
  private ObjectDeclaration currentObject; 
  private Service currentService; 
  private State currentState; 
  
  public Expression resolveName ( String name )
  {
    for ( int i = $NameScope.size() - 1; i >= 0; i-- )
    {
      if ( $NameScope[i]::lookup != null )
      {
        Name result = $NameScope[i]::lookup.find(name);
        if ( result != null ) return result.getReference(getPosition(name));
      }
    }

    return Name.create(name,currentDomain,currentObject,currentService,currentState);
  }

  private Masl masl = null;

  public Walker ( Masl masl, java.io.File file ) throws RecognitionException, java.io.IOException
  {
    super(file);
    this.masl = masl;
  }

}

target                        : definition+;

definition                    : projectDefinition
                              | domainDefinition
                              ;


//---------------------------------------------------------
// Project Definition
//---------------------------------------------------------

identifier
returns [String name]
                              : Identifier                  {
                                                              registerPosition($Identifier);
                                                              $name = $Identifier.text;
                                                            }
                              ;


projectDefinition
returns [Project project]
@init
{
  List<ProjectDomain> domains = new ArrayList<ProjectDomain>();
}
                              : ^( PROJECT
                                   projectName              {
                                                              $project = new Project ( getPosition($PROJECT),$projectName.name );
                                                              currentProject = $project;
                                                            }
                                   ( projectDomainDefinition { if ( $projectDomainDefinition.domain != null )
                                                              {
                                                                project.addDomain($projectDomainDefinition.domain);
                                                              }
                                                            }
                                   )*
                                   pragmaList)              { $project.setPragmas($pragmaList.pragmas); }
                              ;

projectDomainDefinition
returns [ProjectDomain domain]
                              : ^( DOMAIN
                                   projectDomainReference   {
                                                               $domain = new ProjectDomain(
                                                                                          $projectDomainReference.ref, currentProject );
                                                               currentPrjDomain = $domain;
                                                               currentDomain = $domain.getDomain();
                                                            }
                                   ( projectTerminatorDefinition    
                                   )*
                                   pragmaList               { $domain.setPragmas($pragmaList.pragmas); }
                                 )                          { currentPrjDomain = null; currentDomain = null; }
                              ;


projectName
returns [String name]
                              :^( PROJECT_NAME
                                   identifier               { $name = $identifier.name; }
                                )
                              ;


//---------------------------------------------------------
// Domain Definition
//---------------------------------------------------------

domainDefinition
returns [Domain domain]
@after
{
  $domain.setFullyDefined();
  currentDomain = null;                                                                       
}                                                                                             
                              : ^( DOMAIN
                                   domainName                    {
                                                                   $domain = new Domain(getPosition($DOMAIN),$domainName.name);
                                                                   Domains.addDomain($domain);
                                                                   currentDomain = domain;
                                                                 }
                                   ( objectDeclaration           
                                   | domainServiceDeclaration    
                                   | domainTerminatorDefinition    
                                   | relationshipDefinition     
                                   | objectDefinition            
                                   | typeDeclaration             
                                   | typeForwardDeclaration             
                                   | exceptionDeclaration        
                                   )*
                                   pragmaList                    { $domain.setPragmas($pragmaList.pragmas); }
                                 )                              
                              ;

domainName
returns [String name]
                              : ^( DOMAIN_NAME
                                   identifier               { $name = $identifier.name; }
                                 )
                              ;

domainReference
returns [Domain.Reference ref]
                              : domainName                  { 
                                                              Domain domain = Domains.findDomain($domainName.name);
                                                              if ( domain == null )
                                                              {
                                                                try
                                                                {
                                                                  Domain iface = masl.parseInterface($domainName.name);
                                                                  if ( currentDomain != null )
                                                                  {
                                                                    currentDomain.addReferencedInterface(iface);
                                                                  }                                                                                                                        
                                                                }
                                                                catch ( java.io.IOException e ) { }
                                                                domain = Domains.findDomain($domainName.name);
                                                              }
                                                              if ( domain != null )
                                                              {
                                                                $ref = domain.getReference($domainName.name);
                                                              }
                                                            }
                              ;


projectDomainReference
returns [Domain.Reference ref]
                              : domainName                  { 
                                                              Domain domain = Domains.findDomain($domainName.name);
                                                              if ( domain == null )
                                                              {
                                                                try
                                                                {
                                                                  Domain iface = masl.parseProjectDomain($domainName.name);
                                                                }
                                                                catch ( java.io.IOException e ) { }
                                                                domain = Domains.findDomain($domainName.name);
                                                              }
                                                              if ( domain != null )
                                                              {
                                                                $ref = domain.getReference($domainName.name);
                                                              }
                                                            }
                              ;



optionalDomainReference
returns [Domain.Reference ref, boolean defaulted]
                              : domainReference             { $ref = $domainReference.ref; $defaulted = false; }
                              | /* blank */                 { $ref = currentDomain.getReference(null); $defaulted = true;}
                              ;



//---------------------------------------------------------
// Exception Declaration
//---------------------------------------------------------
exceptionDeclaration
                              : ^( EXCEPTION
                                   exceptionName            
                                   exceptionVisibility      
                                   pragmaList               
                                 )                          { 
                                                              ExceptionDeclaration exception = new ExceptionDeclaration( 
                                                                                          getPosition($EXCEPTION),                             
                                                                                          currentDomain,
                                                                                          $exceptionName.name,
                                                                                          $exceptionVisibility.visibility,
                                                                                          $pragmaList.pragmas);
                                                              currentDomain.addException(exception);
                                                            }
                              ;

exceptionName
returns [ String name ]
                              : ^( EXCEPTION_NAME
                                   identifier)              { $name = $identifier.name; }
                              ;

exceptionReference
returns [ExceptionReference ref]
                              : optionalDomainReference
                                exceptionName               { $ref = ExceptionReference.create($optionalDomainReference.ref, $optionalDomainReference.defaulted,$exceptionName.name); }
                              ;
                                

exceptionVisibility
returns [ Visibility visibility ]
                              : PRIVATE                     { $visibility = Visibility.PRIVATE; }
                              | PUBLIC                      { $visibility = Visibility.PUBLIC; }
                              ;

//---------------------------------------------------------
// Type Definition
//---------------------------------------------------------

typeForwardDeclaration
@init
{
  boolean isSubtype = false;
}
                              : ^( TYPE_DECLARATION
                                   typeName                 
                                   typeVisibility
                                   pragmaList				{
                                                              TypeDeclaration declaration = TypeDeclaration.createForwardDeclaration ( getPosition($TYPE_DECLARATION),
                                                                                       currentDomain,
                                                                                       $typeName.name,
                                                                                       $typeVisibility.visibility,
                                                                                       $pragmaList.pragmas );
                                                            }
                                 )                          
                              ;
                              

typeDeclaration
@init
{
  boolean isSubtype = false;
}
                              : ^( TYPE
                                   typeName                 
                                   typeVisibility
                                   pragmaList				{
                                                              TypeDeclaration declaration = TypeDeclaration.getOrCreate ( getPosition($TYPE),
                                                                                       currentDomain,
                                                                                       $typeName.name,
                                                                                       $typeVisibility.visibility,
                                                                                       null,
                                                                                       $pragmaList.pragmas );
                                                            }
                                   typeDefinition			{ declaration.setTypeDefinition($typeDefinition.def); }
                                 )                          
                              ;
                              

typeDefinition
returns [TypeDefinition def]
                              : structureTypeDefinition     { $def = $structureTypeDefinition.type; }
                              | enumerationTypeDefinition   { $def = $enumerationTypeDefinition.type; }
                              | constrainedTypeDefinition   { $def = $constrainedTypeDefinition.type; }
                              | typeReference               { $def = $typeReference.type; }
                              | unconstrainedArrayDefinition{ $def = $unconstrainedArrayDefinition.type; }
                              ;

typeVisibility
returns [Visibility visibility]
                              : PRIVATE                     { $visibility = Visibility.PRIVATE; }
                              | PUBLIC                      { $visibility = Visibility.PUBLIC; }
                              ;



// Constrained Type
constrainedTypeDefinition
returns [ConstrainedType type]
                              : ^( CONSTRAINED_TYPE
                                   typeReference
                                   typeConstraint
                                 )                          {
                                                              if ( $typeReference.type != null && $typeConstraint.constraint != null )
                                                              {
                                                                $type = new ConstrainedType($typeReference.type,
                                                                                          $typeConstraint.constraint);
                                                              }
                                                            }
                              ;

typeConstraint
returns [TypeConstraint constraint]
                              : rangeConstraint             { $constraint = $rangeConstraint.range; }
                              | deltaConstraint             { $constraint = $deltaConstraint.delta; }
                              | digitsConstraint            { $constraint = $digitsConstraint.digits; }
                              ;

rangeConstraint
returns [RangeConstraint range]
                              : ^( RANGE
                                   expression
                                 )                          {
                                                              if ( $expression.exp != null )
                                                              {
                                                                $range = new RangeConstraint($expression.exp);
                                                              }
                                                            }
                              ;

deltaConstraint
returns [DeltaConstraint delta]
                              : ^( DELTA
                                   expression
                                   rangeConstraint
                                 )                          {
                                                              if ( $expression.exp != null && $rangeConstraint.range != null )
                                                              {
                                                                $delta = new DeltaConstraint($expression.exp,$rangeConstraint.range);
                                                              }
                                                            }
                              ;

digitsConstraint
returns [DigitsConstraint digits]
                              : ^( DIGITS
                                   expression
                                   rangeConstraint
                                 )                          {
                                                              if ( $expression.exp != null && $rangeConstraint.range != null )
                                                              {
                                                                $digits = new DigitsConstraint($expression.exp,$rangeConstraint.range);
                                                              }
                                                            }
                              ;

// Structure Type
structureTypeDefinition
returns [StructureType type]
@init
{
  List<StructureElement> elements = new ArrayList<StructureElement>();
}
                              : ^( STRUCTURE
                                   ( structureComponentDefinition 
                                                            {
                                                              elements.add($structureComponentDefinition.element);
                                                            }
                                   )+
                                 )                          { $type = StructureType.create(getPosition($STRUCTURE),elements); }
                              ;


structureComponentDefinition
returns [StructureElement element]
                              : ^( COMPONENT_DEFINITION
                                   componentName
                                   typeReference
                                   expression?
                                   pragmaList
                                 )                          {
                                                                $element = StructureElement.create(
                                                                                          $componentName.name,
                                                                                          $typeReference.type,
                                                                                          $expression.exp,
                                                                                          $pragmaList.pragmas );
                                                            }
                              ;

componentName
returns [String name]
                              : ^( COMPONENT_NAME
                                   identifier
                                 )                          { $name = $identifier.name; }
                              ;


// Enumeration Type
enumerationTypeDefinition
returns [EnumerateType type]
@init
{
  List<EnumerateItem> items = new ArrayList<EnumerateItem>();
}
                              : ^( ENUM
                                   ( enumerator             {items.add($enumerator.item); }
                                   )+
                                 )                          { $type = EnumerateType.create(getPosition($ENUM),items); }
                              ;

enumerator
returns [EnumerateItem item]
                              : ^( ENUMERATOR
                                   enumeratorName
                                   expression?
                                 )                          { $item = new EnumerateItem($enumeratorName.name,$expression.exp); }
                              ;

enumeratorName
returns [String name]         : ^( ENUMERATOR_NAME
                                   identifier
                                 )                          { $name = $identifier.name; }
                              ;


// Unconstrained array
unconstrainedArrayDefinition
returns [UnconstrainedArrayType type]
                              : ^( UNCONSTRAINED_ARRAY
                                   index=typeReference
                                   contained=typeReference
                                 )                          { 
                                                              if ( $contained.type != null && $index.type != null ) 
                                                              {
                                                                $type = new UnconstrainedArrayType(getPosition($UNCONSTRAINED_ARRAY),$contained.type,$index.type);
                                                              }
                                                            }
                              ;

//---------------------------------------------------------
// Type Reference
//---------------------------------------------------------

typeReference
returns [BasicType type]
                              : namedTypeRef                { $type = $namedTypeRef.type; }
                              | constrainedArrayTypeRef     { $type = $constrainedArrayTypeRef.type; }
                              | instanceTypeRef             { $type = $instanceTypeRef.type; }
                              | sequenceTypeRef             { $type = $sequenceTypeRef.type; }
                              | arrayTypeRef                { $type = $arrayTypeRef.type; }
                              | setTypeRef                  { $type = $setTypeRef.type; }
                              | bagTypeRef                  { $type = $bagTypeRef.type; }
                              | dictionaryTypeRef           { $type = $dictionaryTypeRef.type; }
                              ;

instanceTypeRef
returns [InstanceType type]
                              : ^( INSTANCE
                                   objectReference
                                   ANONYMOUS?
                                 )                          { $type = InstanceType.create ( getPosition($INSTANCE),$objectReference.ref, $ANONYMOUS!=null ); }
                              ;

namedTypeRef
returns [BasicType type]
                              : ^( NAMED_TYPE
                                   optionalDomainReference
                                   typeName
                                   ANONYMOUS?
                                 )                          { 
                                                              $type = BasicType.createNamedType ( $optionalDomainReference.ref, $optionalDomainReference.defaulted,$typeName.name, $ANONYMOUS!=null );
                                                            }
                              ;

userDefinedTypeRef
returns [UserDefinedType type]
                              : ^( NAMED_TYPE
                                   optionalDomainReference
                                   typeName
                                 )                          { 
                                                              $type = UserDefinedType.create ( $optionalDomainReference.ref, $typeName.name);
                                                            }
                              ;

constrainedArrayTypeRef
returns [UnconstrainedArraySubtype type]
                              : ^( CONSTRAINED_ARRAY
                                   userDefinedTypeRef
                                   arrayBounds
                                 )                          { $type = UnconstrainedArraySubtype.create (
                                                                                          $userDefinedTypeRef.type,
                                                                                          $arrayBounds.exp );
                                                            }
                              ;


sequenceTypeRef
returns [SequenceType type]
                              : ^( SEQUENCE
                                   typeReference
                                   expression?
                                   ANONYMOUS?
                                 )                          { $type = SequenceType.create ( getPosition($SEQUENCE), $typeReference.type, $expression.exp, $ANONYMOUS!=null ); }
                              ;

arrayTypeRef
returns [ArrayType type]
                              : ^( ARRAY
                                   typeReference
                                   arrayBounds
                                   ANONYMOUS?
                                 )                          { $type = ArrayType.create (getPosition($ARRAY), $typeReference.type, $arrayBounds.exp, $ANONYMOUS!=null ); }
                              ;

setTypeRef
returns [SetType type]
                              : ^( SET
                                   typeReference
                                   ANONYMOUS?
                                 )                          { $type = SetType.create ( getPosition($SET),$typeReference.type, $ANONYMOUS!=null ); }
                              ;

bagTypeRef
returns [BagType type]
                              : ^( BAG
                                   typeReference
                                   ANONYMOUS?
                                 )                          { $type = BagType.create ( getPosition($BAG),$typeReference.type, $ANONYMOUS!=null ); }
                              ;

dictionaryTypeRef
returns [DictionaryType type]
                              : ^( DICTIONARY
                                   (^(KEY   key=typeReference))?
                                   (^(VALUE value=typeReference))?
                                   ANONYMOUS?
                                 )                          { $type = DictionaryType.create ( getPosition($DICTIONARY), 
                                 																															$key.type, 
                                 																															$value.type, 
                                 																															$ANONYMOUS!=null ); }
                              ;
typeName
returns [String name]
                              : ^( TYPE_NAME
                                   identifier )             { $name = $identifier.name; }
                              ;

arrayBounds
returns [Expression exp]
                              : ^( ARRAY_BOUNDS
                                   expression )             { $exp = $expression.exp; }
                              ;

//---------------------------------------------------------
// Terminator Definition
//---------------------------------------------------------

terminatorName
returns [String name]
                              : ^( TERMINATOR_NAME
                                   identifier )             { $name = $identifier.name; }
                              ;


domainTerminatorDefinition
@init
{
  DomainTerminator terminator;
}
                              : ^( TERMINATOR_DEFINITION
                                   terminatorName             
                                   pragmaList                 {
                                   									            terminator = 
                                   									               DomainTerminator.create( getPosition($TERMINATOR_DEFINITION),
                                                                                             currentDomain,
                                                                                             $terminatorName.name,
                                                                                             $pragmaList.pragmas );
                                                              }
                                   ( terminatorServiceDeclaration[terminator] 
                                   )*
                                 )
                              ;

projectTerminatorDefinition
@init
{
  ProjectTerminator terminator;
}
                              : ^( TERMINATOR_DEFINITION
                                   terminatorName             
                                   pragmaList                 {
                                   									            terminator = 
                                   									               ProjectTerminator.create( getPosition($TERMINATOR_DEFINITION),
                                                                                             currentPrjDomain,
                                                                                             $terminatorName.name,
                                                                                             $pragmaList.pragmas );
                                                              }
                                   ( projectTerminatorServiceDeclaration[terminator] 
                                   )*
                                 )
                              ;



terminatorServiceDeclaration[DomainTerminator terminator]
                              : ^( TERMINATOR_SERVICE_DECLARATION
                                   serviceVisibility
                                   serviceName
                                   parameterList
                                   returnType?
                                   pragmaList
                                 )
                                                            {
                                                               DomainTerminatorService.create ( 
                                                                                      getPosition($TERMINATOR_SERVICE_DECLARATION),
                                                                                      terminator,
                                                                                      $serviceName.name,
                                                                                      $serviceVisibility.visibility,
                                                                                      $parameterList.params,
                                                                                      $returnType.type,
                                                                                      Collections.<ExceptionReference>emptyList(),
                                                                                      $pragmaList.pragmas);
                                                            }
                              ;

projectTerminatorServiceDeclaration[ProjectTerminator terminator]
                              : ^( TERMINATOR_SERVICE_DECLARATION
                                   serviceVisibility
                                   serviceName
                                   parameterList
                                   returnType?
                                   pragmaList
                                 )
                                                            {
                                                               ProjectTerminatorService.create ( 
                                                                                      getPosition($TERMINATOR_SERVICE_DECLARATION),
                                                                                      terminator,
                                                                                      $serviceName.name,
                                                                                      $serviceVisibility.visibility,
                                                                                      $parameterList.params,
                                                                                      $returnType.type,
                                                                                      Collections.<ExceptionReference>emptyList(),
                                                                                      $pragmaList.pragmas);
                                                            }
                              ;


//---------------------------------------------------------
// Object Definition
//---------------------------------------------------------

objectName
returns [String name]
                              : ^( OBJECT_NAME
                                   identifier )             { $name = $identifier.name; }
                              ;


objectReference
returns [ObjectNameExpression ref]
                              : optionalDomainReference
                                objectName                  { $ref = ObjectNameExpression.create($optionalDomainReference.ref,$objectName.name); }
                              ;

fullObjectReference
returns [ObjectNameExpression ref]
                              : domainReference
                                objectName                  { $ref = ObjectNameExpression.create($domainReference.ref,$objectName.name); }
                              ;


optionalObjectReference
returns [ObjectNameExpression ref]
                              : objectReference             { $ref = $objectReference.ref; }
                              | /* blank */                 { $ref = (currentObject == null ? null : currentObject.getReference(null)); }
                              ;
attributeName
returns [String name]
                              : ^( ATTRIBUTE_NAME
                                   identifier )             { $name = $identifier.name; }
                              ;

objectDeclaration
                              : ^( OBJECT_DECLARATION
                                   objectName 
                                   pragmaList
                                 )                          {
                                                              ObjectDeclaration.create( getPosition($OBJECT_DECLARATION),
                                                                                        currentDomain,
                                                                                        $objectName.name,$pragmaList.pragmas);
                                                            }
                                 
                              ;


objectDefinition
@after
{
  currentObject.setFullyDefined();
  currentObject = null;                                                                       
}                                                                                             
                              : ^( OBJECT_DEFINITION
                                   objectName               {
                                                              try
                                                              {
                                                                currentObject = currentDomain.getObject($objectName.name);
                                                                currentObject.setPosition(getPosition($objectName.name));
                                                              }
                                                              catch ( SemanticError e ) { e.report(); }
                                                            }
                                   ( attributeDefinition      
                                   | identifierDefinition     
                                   | objectServiceDeclaration 
                                   | eventDefinition          
                                   | stateDeclaration         
                                   | transitionTable          
                                   )*
                                   pragmaList                 { if ( currentObject != null ) currentObject.setDefinitionPragmas( $pragmaList.pragmas ); }
                                 )
                              ;

attributeDefinition
@init
{
  List<ReferentialAttributeDefinition> refAtts = new ArrayList<ReferentialAttributeDefinition>();
}
                              : ^( ATTRIBUTE_DEFINITION
                                   attributeName            
                                   PREFERRED? UNIQUE?
                                   ( attReferential         { refAtts.add($attReferential.ref); }
                                   )*
                                   typeReference
                                   expression?
                                   pragmaList
                                 )                          {
                                                               AttributeDeclaration.create(
                                                                                          currentObject,
                                                                                          $attributeName.name,
                                                                                          $typeReference.type,
                                                                                          $PREFERRED!=null,
                                                                                          $UNIQUE!=null,
                                                                                          refAtts,
                                                                                          $expression.exp,
                                                                                          $pragmaList.pragmas);
                                                             }

                              ;

attReferential
returns [ReferentialAttributeDefinition ref]
                              : ^( REFERENTIAL
                                   relationshipSpec[new ObjectNameExpression(null,currentObject),false,false]
                                   attributeName
                                 )                          {
                                                              $ref = ReferentialAttributeDefinition.create (
                                                                                        currentObject,
                                                                                        $relationshipSpec.rel,
                                                                                        $attributeName.name );
                                                            }
                              ;


relationshipSpec[Expression lhs, boolean allowToAssoc, boolean forceToAssoc]
returns [RelationshipSpecification.Reference rel]
                              : ^( RELATIONSHIP_SPEC
                                   relationshipReference    
                                   ( objOrRole objectReference? )?
                                 ) 													{ $rel = RelationshipSpecification.createReference(lhs,$relationshipReference.ref,$objOrRole.name,$objectReference.ref,allowToAssoc,forceToAssoc); }
                              ;

objOrRole
returns [String name]
                              : identifier                  { $name = $identifier.name; }
                              ;


objectServiceDeclaration
                              : ^( OBJECT_SERVICE_DECLARATION
                                   serviceVisibility
                                   ( INSTANCE
                                     relationshipReference?)?
                                   serviceName
                                   parameterList
                                   returnType?
                                   pragmaList
                                 )                          {
                                                              ObjectService.create ( getPosition($OBJECT_SERVICE_DECLARATION),
                                                                                     currentObject,
                                                                                     $serviceName.name,
                                                                                     $serviceVisibility.visibility,
                                                                                     $INSTANCE!=null,
                                                                                     $relationshipReference.ref,
                                                                                     $parameterList.params,
                                                                                     $returnType.type,
                                                                                     Collections.<ExceptionReference>emptyList(),
                                                                                     $pragmaList.pragmas);
                                                            }
                              ;


identifierDefinition
@init
{
  List<String> atts = new ArrayList<String>();
}
                              : ^( IDENTIFIER
                                   ( attributeName     { atts.add($attributeName.name); }
                                   )+
                                   pragmaList
                                 )                     { IdentifierDeclaration.create(getPosition($IDENTIFIER),currentObject,atts,$pragmaList.pragmas); }
                              ;

eventDefinition
                              : ^( EVENT         
                                   eventName                
                                   eventType                
                                   parameterList
                                   pragmaList               
                                 )
                                                            {
                                                              EventDeclaration.create( getPosition($EVENT),
                                                                                       currentObject,
                                                                                       $eventName.name, 
                                                                                       $eventType.type, 
                                                                                       $parameterList.params,
                                                                                       $pragmaList.pragmas );
                                                            }
                              ;

eventName
returns [String name]
                              : ^( EVENT_NAME
                                   identifier )             { $name = $identifier.name; }
                              ;

eventType
returns [EventType type]
                              : ASSIGNER                    { $type = EventType.ASSIGNER; }
                              | CREATION                    { $type = EventType.CREATION; }
                              | NORMAL                      { $type = EventType.NORMAL; }
                              ;

stateDeclaration
                              : ^( STATE
                                   stateName                
                                   stateType               
                                   parameterList
                                   pragmaList              
                                )                           {
                                                              State.create( getPosition($STATE),
                                                                                 currentObject,
                                                                                 $stateName.name,
                                                                                 $stateType.type,
                                                                                 $parameterList.params,
                                                                                 $pragmaList.pragmas );
                                                            }
                              ;

stateName
returns [String name]
                              : ^( STATE_NAME
                                   identifier )             { $name = $identifier.name; }
                              ;

stateType
returns [StateType type]
                              : ASSIGNER                    { $type = StateType.ASSIGNER; }
                              | START                       { $type = StateType.ASSIGNER_START; }
                              | CREATION                    { $type = StateType.CREATION; }
                              | TERMINAL                    { $type = StateType.TERMINAL; }
                              | NORMAL                      { $type = StateType.NORMAL; }
                              ;


transitionTable
@init
{
 List<TransitionRow> rows = new ArrayList<TransitionRow>();
}
                              : ^( TRANSITION_TABLE
                                   transTableType
                                   ( transitionRow          { rows.add($transitionRow.row); }
                                   )+
                                   pragmaList
                                 )                          {
                                                              TransitionTable.create( getPosition($TRANSITION_TABLE),
                                                              												currentObject,
                                                                                      $transTableType.isAssigner,
                                                                                      rows,
                                                                                      $pragmaList.pragmas);
                                                            }
                              ;


transTableType
returns [boolean isAssigner]
                              : ASSIGNER                    { $isAssigner = true; }
                              | NORMAL                      { $isAssigner = false; }
                              ;

transitionRow
returns [TransitionRow row]
@init
{
 List<TransitionOption> options = new ArrayList<TransitionOption>();
}
                              : ^( TRANSITION_ROW
                                   startState
                                   ( transitionOption       { options.add($transitionOption.option); }
                                   )+
                                   pragmaList
                                )                           {
                                                                $row = TransitionRow.create ( currentObject,
                                                                												      $startState.name,
                                                                                              options,
                                                                                              $pragmaList.pragmas);
                                                            }
                              ;

transitionOption
returns [TransitionOption option]
                              : ^( TRANSITION_OPTION
                                   incomingEvent
                                   endState
                                 )                          {
                                                                $option = TransitionOption.create (
                                                                                          $incomingEvent.ref,
                                                                                          currentObject,
                                                                                          $endState.type,
                                                                                          $endState.name );
                                                            }
                              ;

incomingEvent
returns [EventExpression ref]
                              : ^( EVENT
                                   eventReference           { $ref  = $eventReference.ref; }
                                 )
                              ;

startState
returns [String name]
                              : NON_EXISTENT
                              | stateName                   { $name = $stateName.name; }
                              ;

endState
returns [String name, TransitionType type]
                              : stateName                   { $name = $stateName.name;  
                                                              $type = TransitionType.TO_STATE; }
                              | IGNORE                      { $type = TransitionType.IGNORE; }
                              | CANNOT_HAPPEN               { $type = TransitionType.CANNOT_HAPPEN; }
                              ;

eventReference
returns [EventExpression ref]
                              : optionalObjectReference
                                eventName                   { $ref = EventExpression.create ( $optionalObjectReference.ref, $eventName.name);
                                                            }
                              ;


//---------------------------------------------------------
// Service Declaration
//---------------------------------------------------------

domainServiceDeclaration
                              : ^( DOMAIN_SERVICE_DECLARATION
                                   serviceVisibility
                                   serviceName
                                   parameterList
                                   returnType?
                                   pragmaList
                                 )
                                                            {
                                                               DomainService.create ( getPosition($DOMAIN_SERVICE_DECLARATION),
                                                                                      currentDomain,
                                                                                      $serviceName.name,
                                                                                      $serviceVisibility.visibility,
                                                                                      $parameterList.params,
                                                                                      $returnType.type,
                                                                                      Collections.<ExceptionReference>emptyList(),
                                                                                      $pragmaList.pragmas);
                                                            }
                              ;


parameterDefinition
returns [ParameterDefinition parameter]
                              : ^( PARAMETER_DEFINITION
                                   parameterName
                                   parameterMode
                                   parameterType)           {
                                                              $parameter = ParameterDefinition.create (
                                                                                          $parameterName.name,
                                                                                          $parameterMode.mode,
                                                                                          $parameterType.type);
                                                            }
                              ;
                              
parameterList
returns [List<ParameterDefinition> params]
@init
{
  params = new ArrayList<ParameterDefinition>();
}
                              : ( parameterDefinition       { $params.add($parameterDefinition.parameter); }
                                )*
                              ;


serviceVisibility
returns [Visibility visibility]
                              : PRIVATE                     { $visibility = Visibility.PRIVATE; }
                              | PUBLIC                      { $visibility = Visibility.PUBLIC; }
                              ;

parameterMode
returns [ParameterModeType mode]
                              : IN                          { $mode = ParameterModeType.IN; }
                              | OUT                         { $mode = ParameterModeType.OUT; }
                              ;


serviceName
returns [String name]
                              : ^( SERVICE_NAME
                                   identifier )             { $name = $identifier.name; }
                              ;

parameterName
returns [String name]
                              : ^( PARAMETER_NAME
                                   identifier )             { $name = $identifier.name; }
                              ;

parameterType
returns [BasicType type]
                              : ^( PARAMETER_TYPE
                                   typeReference)           { $type = $typeReference.type; }
                              ;

returnType
returns [BasicType type]
                              : ^( RETURN_TYPE
                                   typeReference)           { $type = $typeReference.type; }
                              ;


//---------------------------------------------------------
// Relationship Definition
//---------------------------------------------------------


relationshipDefinition
                              : regularRelationshipDefinition
                              | assocRelationshipDefinition   
                              | subtypeRelationshipDefinition 
                              ;



regularRelationshipDefinition
                              : ^( REGULAR_RELATIONSHIP_DEFINITION
                                   relationshipName
                                   leftToRight=halfRelationshipDefinition
                                   rightToLeft=halfRelationshipDefinition
                                   pragmaList
                                 )                          {
                                 														  NormalRelationshipDeclaration.create (
                                                                                          getPosition($REGULAR_RELATIONSHIP_DEFINITION),
                                                                                          currentDomain,
                                                                                          $relationshipName.name,
                                                                                          $leftToRight.half,
                                                                                          $rightToLeft.half,
                                                                                          $pragmaList.pragmas );
                                                            }
                              ;


assocRelationshipDefinition
                              : ^( ASSOCIATIVE_RELATIONSHIP_DEFINITION
                                   relationshipName
                                   leftToRight=halfRelationshipDefinition
                                   rightToLeft=halfRelationshipDefinition
                                   assocObj=objectReference
                                   pragmaList
                                 )                          {
                                                              AssociativeRelationshipDeclaration.create (
                                                                                          getPosition($ASSOCIATIVE_RELATIONSHIP_DEFINITION),
                                                                                          currentDomain,
                                                                                          $relationshipName.name,
                                                                                          $leftToRight.half,
                                                                                          $rightToLeft.half,
                                                                                          $assocObj.ref,
                                                                                          $pragmaList.pragmas );
                                                            }

                              ;

halfRelationshipDefinition
returns [HalfRelationship half]
                              : ^( HALF_RELATIONSHIP
                                   from=objectReference
                                   conditionality
                                   rolePhrase
                                   multiplicity
                                   to=objectReference
                                 )                          {
                                                              $half = HalfRelationship.create (
                                                                                          $from.ref,
                                                                                          $conditionality.cond,
                                                                                          $rolePhrase.role,
                                                                                          $multiplicity.mult,
                                                                                          $to.ref );
                                                            }
                              ;


subtypeRelationshipDefinition
returns [SubtypeRelationshipDeclaration relationship]
@init
{
  List<ObjectNameExpression> subtypes = new ArrayList<ObjectNameExpression>();
}
                              : ^( SUBTYPE_RELATIONSHIP_DEFINITION
                                   relationshipName
                                   supertype=objectReference
                                   (subtype=objectReference { subtypes.add($subtype.ref); }  
                                   )+
                                   pragmaList
                                 )                          {
                                                              SubtypeRelationshipDeclaration.create (
                                                                                          getPosition($SUBTYPE_RELATIONSHIP_DEFINITION),
                                                                                          currentDomain,
                                                                                          $relationshipName.name,
                                                                                          $supertype.ref,
                                                                                          subtypes,
                                                                                          $pragmaList.pragmas );
                                                            }

                              ;

rolePhrase
returns [String role]
                              : ^( ROLE_PHRASE
                                   identifier )             { $role = $identifier.name; }
                              ;

conditionality
returns [boolean cond]
                              : UNCONDITIONALLY             { $cond = false; }
                              | CONDITIONALLY               { $cond = true; }
                              ;

multiplicity
returns [MultiplicityType mult]
                              : ONE                         { $mult = MultiplicityType.ONE; }
                              | MANY                        { $mult = MultiplicityType.MANY; }
                              ;


relationshipName
returns [String name]
                              : ^( RELATIONSHIP_NAME
                                   RelationshipName  
                                 )                          {
                                                              registerPosition($RelationshipName);
                                                              $name = $RelationshipName.text;
                                                            }
                              ;
                              

relationshipReference
returns [RelationshipDeclaration.Reference ref]
                              : optionalDomainReference
                                relationshipName            { $ref = RelationshipDeclaration.createReference($optionalDomainReference.ref,$relationshipName.name); }
                              ;


//---------------------------------------------------------
// Pragma Definition
//---------------------------------------------------------


pragmaList
returns [ PragmaList pragmas ]
@init
{
  List<PragmaDefinition> list = new ArrayList<PragmaDefinition>();
}
                              : ( pragma                    { list.add($pragma.def); }
                                )*                          { $pragmas = new PragmaList(list); }
                              ;

pragma
returns [PragmaDefinition def]
@init
{
  List<String> values = new ArrayList<String>();
}
                              : ^( PRAGMA
                                   pragmaName
                                   ( pragmaValue            { values.add($pragmaValue.value); }
                                   )*
                                 )                          { $def = new PragmaDefinition($pragmaName.name,values); }
                              ;

pragmaValue
returns [ String value ]
                              : identifier                  { $value = $identifier.name; }
                              | literalExpression           {
                                                              if ( $literalExpression.exp instanceof StringLiteral ) 
                                                              {
                                                                $value = ((StringLiteral)$literalExpression.exp).getValue();
                                                              }
                                                              else if ( $literalExpression.exp instanceof CharacterLiteral ) 
                                                              {
                                                                $value = "" + ((CharacterLiteral)$literalExpression.exp).getValue();
                                                              }
                                                              else
                                                              {
                                                               $value = $literalExpression.text;
                                                              }
                                                            }
                              ;

pragmaName
returns [ String name ]
                              : ^( PRAGMA_NAME
                                   identifier               { $name = $identifier.name; }
                                 )
                              ;

//---------------------------------------------------------
// Dynamic Behaviour
//---------------------------------------------------------



domainServiceDefinition[DomainService service]
scope NameScope;
@init
{
  currentService = service;
  currentDomain = service.getDomain();
  $NameScope::lookup = service.getNameLookup();
}
@after
{
  currentService = null;
  currentDomain = null;
}
                              : ^( DOMAIN_SERVICE_DEFINITION
                                   serviceVisibility
                                   domainReference
                                   serviceName
                                   parameterList
                                   returnType?
                                   codeBlock[false]
                                   pragmaList               { if ( $codeBlock.st != null )  $codeBlock.st.setPragmas($pragmaList.pragmas); }   
                                 )                          { if ( $codeBlock.st != null )  service.setCode($codeBlock.st); }                         
                                                            
                              ;


terminatorServiceDefinition[DomainTerminatorService service]
scope NameScope;
@init
{
  currentService = service;
  currentDomain = service.getTerminator().getDomain();
  $NameScope::lookup = service.getNameLookup();
}
@after
{
  currentService = null;
  currentDomain = null;
}
                              : ^( TERMINATOR_SERVICE_DEFINITION
                                   serviceVisibility
                                   domainReference
                                   terminatorName
                                   serviceName
                                   parameterList
                                   returnType?
                                   codeBlock[false]
                                   pragmaList               { if ( $codeBlock.st != null )  $codeBlock.st.setPragmas($pragmaList.pragmas); }   
                                 )                          { if ( $codeBlock.st != null )  service.setCode($codeBlock.st); }                         
                                                            
                              ;


projectTerminatorServiceDefinition[ProjectTerminatorService service]
scope NameScope;
@init
{
  currentService = service;
  currentDomain = service.getTerminator().getDomainTerminator().getDomain();
  $NameScope::lookup = service.getNameLookup();
}
@after
{
  currentService = null;
  currentDomain = null;
}
                              : ^( TERMINATOR_SERVICE_DEFINITION
                                   serviceVisibility
                                   domainReference
                                   terminatorName
                                   serviceName
                                   parameterList
                                   returnType?
                                   codeBlock[false]
                                   pragmaList               { if ( $codeBlock.st != null )  $codeBlock.st.setPragmas($pragmaList.pragmas); }   
                                 )                          { if ( $codeBlock.st != null )  service.setCode($codeBlock.st); }                         
                                                            
                              ;



objectServiceDefinition[ObjectService service]
scope NameScope;
@init
{
  currentService = service;
  currentObject = service.getParentObject();
  currentDomain = currentObject.getDomain();
  $NameScope::lookup = service.getNameLookup();
}
@after
{
  currentDomain = null;
  currentObject = null;
  currentService = null;
}
                              :^( OBJECT_SERVICE_DEFINITION
                                   serviceVisibility
                                   INSTANCE?
                                   fullObjectReference
                                   serviceName
                                   parameterList
                                   returnType?
                                   codeBlock[false]
                                   pragmaList               { if ( $codeBlock.st != null )  $codeBlock.st.setPragmas($pragmaList.pragmas); }            
                                 )                          { if ( $codeBlock.st != null )  service.setCode($codeBlock.st); }
                              ;


stateDefinition[State stateDef]
scope NameScope;
@init
{
  currentObject = stateDef.getParentObject();
  currentState  = stateDef;
  currentDomain = currentObject.getDomain();
  $NameScope::lookup = stateDef.getNameLookup();
}
@after
{
  currentDomain = null;
  currentObject = null;
  currentState  = null;
}
                              : ^( STATE_DEFINITION
                                   stateType
                                   fullObjectReference
                                   stateName
                                   parameterList
                                   codeBlock[false]
                                   pragmaList               { if ( $codeBlock.st != null )  $codeBlock.st.setPragmas($pragmaList.pragmas); } 
                                 )                          { if ( $codeBlock.st != null )  stateDef.setCode($codeBlock.st); }
                              ;



//---------------------------------------------------------
// Statements
//---------------------------------------------------------

statement
returns [Statement st]
                              : ^( STATEMENT
                                   ( codeBlock[false]       { $st = $codeBlock.st; }
                                   | assignmentStatement    { $st = $assignmentStatement.st; }
                                   | streamStatement        { $st = $streamStatement.st; }
                                   | callStatement          { $st = $callStatement.st; }
                                   | exitStatement          { $st = $exitStatement.st; }
                                   | returnStatement        { $st = $returnStatement.st; }
                                   | delayStatement         { $st = $delayStatement.st; }
                                   | raiseStatement         { $st = $raiseStatement.st; }
                                   | deleteStatement        { $st = $deleteStatement.st; }
                                   | eraseStatement         { $st = $eraseStatement.st; }
                                   | linkStatement          { $st = $linkStatement.st; }
                                   | scheduleStatement      { $st = $scheduleStatement.st; }
                                   | cancelTimerStatement   { $st = $cancelTimerStatement.st; }
                                   | generateStatement      { $st = $generateStatement.st; }
                                   | ifStatement            { $st = $ifStatement.st; }
                                   | caseStatement          { $st = $caseStatement.st; }
                                   | forStatement           { $st = $forStatement.st; }
                                   | whileStatement         { $st = $whileStatement.st; }
                                   |                        { $st = new PragmaStatement(); }
                                   )
                                   pragmaList               { if ( $st != null ) $st.setPragmas($pragmaList.pragmas); }
                                 )
                              ;

statementList
returns [List<Statement> statements = new ArrayList<Statement>()]
                              : ^( STATEMENT_LIST
                                   ( statement                 { if ( $statement.st != null ) $statements.add($statement.st); }
                                   )*
                                 )
                              ;


assignmentStatement
returns [AssignmentStatement st]
                              : ^( ASSIGN
                                   lhs=expression rhs=expression
                                 )                          { $st = AssignmentStatement.create ( getPosition($ASSIGN),$lhs.exp,$rhs.exp ); }
                              ;

streamStatement
returns [IOStreamStatement st]
@init
{
  List<IOStreamStatement.IOExpression> args = new ArrayList<IOStreamStatement.IOExpression>();
}
                              : ^( STREAM_STATEMENT
                                   expression
                                   ( streamOperator         { args.add($streamOperator.op); }
                                   )+
                                 )                          { $st = IOStreamStatement.create ( $expression.exp,args ); }
                              ;

streamOperator
returns [IOStreamStatement.IOExpression op]
@init
{
  IOStreamStatement.ImplType type = null;
}
                              : ^( ( STREAM_IN              { type = IOStreamStatement.ImplType.IN;}
                                   | STREAM_OUT             { type = IOStreamStatement.ImplType.OUT;}
                                   | STREAM_LINE_IN         { type = IOStreamStatement.ImplType.LINE_IN;}
                                   | STREAM_LINE_OUT        { type = IOStreamStatement.ImplType.LINE_OUT;}
                                   ) expression
                                 )                          { 
                                                              $op = IOStreamStatement.createExpression( 
                                                                                          getPosition($STREAM_IN,$STREAM_OUT,$STREAM_LINE_IN,$STREAM_LINE_OUT),
                                                                                          type, $expression.exp );
                                                            }
                              ;

callStatement
returns [Statement st]
@init
{
  List<Expression> args = new ArrayList<Expression>();
}
                              : ^( CALL
                                   expression               
                                   ( argument               { args.add($argument.arg); }
                                   )*                       
                                 )                          { $st = ServiceInvocation.create ( getPosition($CALL), $expression.exp, args ); }

                              ;


exitStatement
returns [ExitStatement st]
                              : ^( EXIT
                                   condition?
                                 )                          { $st = ExitStatement.create ( getPosition($EXIT), $condition.exp ); }
                              ;

returnStatement
returns [ReturnStatement st]
                              : ^( RETURN
                                   expression              
                                 )                          { $st = ReturnStatement.create ( getPosition($RETURN), currentService, $expression.exp ); }
                              ;

delayStatement
returns [DelayStatement st]
                              : ^( DELAY
                                   expression
                                 )                         { $st = DelayStatement.create ( getPosition($DELAY), $expression.exp ); }
                              ;

raiseStatement
returns [RaiseStatement st]
                              : ^( RAISE
                                   exceptionReference
                                   expression?
                                 )                          { $st = RaiseStatement.create ( getPosition($RAISE), $exceptionReference.ref, $expression.exp ); }
                              ;

deleteStatement
returns [DeleteStatement st]
                              : ^( DELETE
                                   expression
                                 )                          { $st = DeleteStatement.create ( getPosition($DELETE), $expression.exp ); }
                              ;

eraseStatement
returns [EraseStatement st]
                              : ^( ERASE
                                   expression
                                 )                          { $st = EraseStatement.create ( getPosition($ERASE), $expression.exp ); }
                              ;

linkStatement
returns [LinkUnlinkStatement st]
                              : ^( linkStatementType
                                   lhs=expression      
                                   relationshipSpec[$lhs.exp,false,false]
                                   (rhs=expression
                                    assoc=expression? )?
                                 )                          { $st = LinkUnlinkStatement.create ( $linkStatementType.pos, 
                                                                                                 $linkStatementType.type,
                                                                                                 $lhs.exp,
                                                                                                 $relationshipSpec.rel,
                                                                                                 $rhs.exp,
                                                                                                 $assoc.exp );
                                                            }
                              ;

linkStatementType
returns [LinkUnlinkStatement.Type type, Position pos]
                              : LINK                        { $type =  LinkUnlinkStatement.LINK; $pos=getPosition($LINK); }
                              | UNLINK                      { $type =  LinkUnlinkStatement.UNLINK; $pos=getPosition($UNLINK); }
                              ;


cancelTimerStatement
returns [CancelTimerStatement st]
                             : ^( CANCEL
                                  timerId=expression )    { $st = CancelTimerStatement.create ( getPosition($CANCEL),
                                                                                                $timerId.exp );
                                                          }
                             ;
                              
scheduleStatement
returns [ScheduleStatement st]
                              : ^( SCHEDULE
                                   timerId=expression
                                   generateStatement
                                   scheduleType
                                   time=expression
                                   period=expression?
                                 )                         { $st = ScheduleStatement.create ( getPosition($SCHEDULE), 
                                                                                              $timerId.exp, 
                                                                                              $generateStatement.st,
                                                                                              $scheduleType.isAbsolute,
                                                                                              $time.exp,
                                                                                              $period.exp );
                                                           }
                              ;
scheduleType
returns [boolean isAbsolute]
                              : AT											    { $isAbsolute = true; }
                              | DELAY									      { $isAbsolute = false; }
                              ;



generateStatement
returns [GenerateStatement st]
@init
{
  List<Expression> args = new ArrayList<Expression>();
}
                              : ^( GENERATE
                                   eventReference
                                   ( argument               { if ( $argument.arg != null ) args.add($argument.arg); }
                                   )*                       
                                   expression? )            { $st = GenerateStatement.create ( getPosition($GENERATE), $eventReference.ref, args, $expression.exp ); }
                              ;

ifStatement
returns [IfStatement st]
@init
{
  List<IfStatement.Branch> branches = new ArrayList<IfStatement.Branch>();
}
                              : ^( IF
                                   condition
                                   statementList            {
                                                              IfStatement.Branch branch = IfStatement.createIfBranch ( getPosition($IF),$condition.exp,$statementList.statements);
                                                              if ( branch != null ) branches.add(branch);
                                                            }
                                   ( elsifBlock             { if ( $elsifBlock.branch != null ) branches.add($elsifBlock.branch); }
                                   )*              
                                   ( elseBlock              { if ( $elseBlock.branch != null ) branches.add($elseBlock.branch); }
                                   )?
                                 )                          { $st = IfStatement.create(getPosition($IF),branches); }
                              ;


elsifBlock
returns [IfStatement.Branch branch]
                              : ^( ELSIF
                                   condition
                                   statementList )          {
                                                              $branch = IfStatement.createIfBranch ( getPosition($ELSIF),$condition.exp,$statementList.statements);
                                                            }
                              ;

elseBlock
returns [IfStatement.Branch branch]
                              : ^( ELSE
                                   statementList )          {
                                                              $branch = IfStatement.createElseBranch ( getPosition($ELSE),$statementList.statements);
                                                            }
                              ;


whileStatement
returns [WhileStatement st]
                              : ^( WHILE
                                   condition
                                   statementList )          { $st = WhileStatement.create ( getPosition($WHILE), $condition.exp, $statementList.statements ); }
                              ;

condition
returns [Expression exp]      : ^( CONDITION
                                   expression )             { $exp = $expression.exp; }
                              ;


caseStatement
returns [CaseStatement st]      
@init
{ 
  List<CaseStatement.Alternative> altList = new ArrayList<CaseStatement.Alternative>();
}
                              :^( CASE
                                  expression
                                  ( caseAlternative         { if ( $caseAlternative.alt != null ) altList.add($caseAlternative.alt); }
                                  )*
                                  ( caseOthers              { if ( $caseOthers.alt != null ) altList.add($caseOthers.alt); }
                                  )?
                                )                           { $st = CaseStatement.create ( getPosition($CASE), $expression.exp, altList ); }
                              ;

caseAlternative
returns [CaseStatement.Alternative alt]
@init
{ 
  List<Expression> choiceList = new ArrayList<Expression>();
}
                              : ^( WHEN                     
                                   ( choice                { choiceList.add($choice.exp); } 
                                   )+
                                   statementList )         { $alt = CaseStatement.createAlternative( getPosition($WHEN), choiceList, $statementList.statements ); }
                              ;

choice
returns [Expression exp]      : ^( CHOICE
                                   expression)              { $exp = $expression.exp; }
                              ;

caseOthers
returns [CaseStatement.Alternative alt]
                              : ^( OTHERS
                                   statementList )          { $alt = CaseStatement.createOther ( getPosition($OTHERS), $statementList.statements ); }
                              ;

forStatement
returns [ForStatement st]
scope NameScope;
                              : ^( FOR
                                   loopVariableSpec         { 
                                                              $st = ForStatement.create ( getPosition($FOR), $loopVariableSpec.spec ); 
                                                              if ( $st != null ) $NameScope::lookup = $st.getNameLookup();
                                                            }
                                   ^( STATEMENT_LIST 
                                      ( statement              { if ( $st != null ) $st.addStatement($statement.st); } 
                                      )*
                                    ) 
                                 )
                              ;

loopVariableSpec
returns [LoopSpec spec]
                              : ^( LOOP_VARIABLE
                                   identifier
                                   REVERSE?
                                   expression )             { $spec = LoopSpec.create ( $identifier.name, $REVERSE==null?false:true, $expression.exp); }
                              ;



//---------------------------------------------------------
// Code Blocks
//---------------------------------------------------------

codeBlock[boolean topLevel]
returns [ CodeBlock st ]
scope NameScope;
                              :^( CODE_BLOCK                { $st = new CodeBlock(getPosition($CODE_BLOCK),$topLevel); $NameScope::lookup = $st.getNameLookup(); }
                                  ( variableDeclaration     { $st.addVariableDefinition($variableDeclaration.var); }
                                  )*     
                                  ^(STATEMENT_LIST ( statement               { $st.addStatement($statement.st); }
                                  )* )
                                  ( exceptionHandler        { $st.addExceptionHandler($exceptionHandler.handler); }
                                  )*
                                  ( otherHandler            { $st.addExceptionHandler($otherHandler.handler); }
                                  )?
                                )
                              ;



variableDeclaration
returns [VariableDefinition var]
                              : ^( VARIABLE_DECLARATION
                                   variableName
                                   READONLY?
                                   typeReference
                                   expression?
                                   pragmaList )             { $var = VariableDefinition.create ( $variableName.name, $typeReference.type, $READONLY==null?false:true, $expression.exp, $pragmaList.pragmas); }
                              ;


exceptionHandler
returns [ExceptionHandler handler]
scope NameScope;
                              : ^( EXCEPTION_HANDLER
                                   exceptionReference
                                   variableName?            { $handler = ExceptionHandler.create ( $exceptionReference.ref, $variableName.name );
                                                              if ( $variableName.name != null ) {
                                                                $NameScope::lookup = $handler.getNameLookup();
                                                              }
                                                            }
                                   ^(STATEMENT_LIST ( statement              { if ( $handler != null ) $handler.addStatement($statement.st); } 
                                   )* )
                                 )
                              ;

otherHandler
returns [ExceptionHandler handler]
                              : ^( OTHER_HANDLER
                                   variableName?            { $handler = ExceptionHandler.create ( getPosition($OTHER_HANDLER), $variableName.name );
                                                              if ( $variableName.name != null ) {
                                                                $NameScope::lookup = $handler.getNameLookup();
                                                              }
                                                            }
                                   ^(STATEMENT_LIST ( statement              { if ( $handler != null ) $handler.addStatement($statement.st); }
                                   )* )
                                 )
                              ;

variableName
returns [String name]
                              : ^( VARIABLE_NAME
                                   identifier )             { $name = $identifier.name; }
                              ;

//---------------------------------------------------------
// Expression Definition
//---------------------------------------------------------


expression
returns [Expression exp]
                              : binaryExpression            { $exp = $binaryExpression.exp; }
                              | unaryExpression             { $exp = $unaryExpression.exp; }
                              | rangeExpression             { $exp = $rangeExpression.exp; }
                              | aggregateExpression         { $exp = $aggregateExpression.exp; }
                              | linkExpression              { $exp = $linkExpression.exp; }
                              | navigateExpression          { $exp = $navigateExpression.exp; }
                              | correlateExpression         { $exp = $correlateExpression.exp; }
                              | orderByExpression           { $exp = $orderByExpression.exp; }
                              | createExpression            { $exp = $createExpression.exp; }
                              | findExpression              { $exp = $findExpression.exp; }
                              | dotExpression               { $exp = $dotExpression.exp; }
                              | terminatorServiceExpression { $exp = $terminatorServiceExpression.exp; }
                              | callExpression              { $exp = $callExpression.exp; }
                              | sliceExpression             { $exp = $sliceExpression.exp; }
                              | primeExpression             { $exp = $primeExpression.exp; }
                              | nameExpression              { $exp = $nameExpression.exp; }
                              | literalExpression           { $exp = $literalExpression.exp; }
                              ;

binaryExpression
returns [BinaryExpression exp]
                              : ^( binaryOperator
                                   lhs=expression
                                   rhs=expression
                                 )                          { $exp = BinaryExpression.create ( $lhs.exp, $binaryOperator.op, $rhs.exp); }
                              ;


binaryOperator
returns [BinaryExpression.OperatorRef op]
                              : AND                         { $op = new BinaryExpression.OperatorRef(getPosition($AND            ),BinaryExpression.ImplOperator.AND); }               
                              | CONCATENATE                 { $op = new BinaryExpression.OperatorRef(getPosition($CONCATENATE    ),BinaryExpression.ImplOperator.CONCATENATE); }
                              | DISUNION                    { $op = new BinaryExpression.OperatorRef(getPosition($DISUNION       ),BinaryExpression.ImplOperator.DISUNION); }
                              | DIVIDE                      { $op = new BinaryExpression.OperatorRef(getPosition($DIVIDE         ),BinaryExpression.ImplOperator.DIVIDE); }
                              | EQUAL                       { $op = new BinaryExpression.OperatorRef(getPosition($EQUAL          ),BinaryExpression.ImplOperator.EQUAL); }
                              | GT                          { $op = new BinaryExpression.OperatorRef(getPosition($GT             ),BinaryExpression.ImplOperator.GREATER_THAN); }
                              | GTE                         { $op = new BinaryExpression.OperatorRef(getPosition($GTE            ),BinaryExpression.ImplOperator.GREATER_THAN_OR_EQUAL); }
                              | INTERSECTION                { $op = new BinaryExpression.OperatorRef(getPosition($INTERSECTION   ),BinaryExpression.ImplOperator.INTERSECTION); }
                              | LT                          { $op = new BinaryExpression.OperatorRef(getPosition($LT             ),BinaryExpression.ImplOperator.LESS_THAN); }
                              | LTE                         { $op = new BinaryExpression.OperatorRef(getPosition($LTE            ),BinaryExpression.ImplOperator.LESS_THAN_OR_EQUAL); }
                              | MINUS                       { $op = new BinaryExpression.OperatorRef(getPosition($MINUS          ),BinaryExpression.ImplOperator.MINUS); }
                              | MOD                         { $op = new BinaryExpression.OperatorRef(getPosition($MOD            ),BinaryExpression.ImplOperator.MOD); }
                              | NOT_EQUAL                   { $op = new BinaryExpression.OperatorRef(getPosition($NOT_EQUAL      ),BinaryExpression.ImplOperator.NOT_EQUAL); }
                              | NOT_IN                      { $op = new BinaryExpression.OperatorRef(getPosition($NOT_IN         ),BinaryExpression.ImplOperator.NOT_IN); }
                              | OR                          { $op = new BinaryExpression.OperatorRef(getPosition($OR             ),BinaryExpression.ImplOperator.OR); }
                              | PLUS                        { $op = new BinaryExpression.OperatorRef(getPosition($PLUS           ),BinaryExpression.ImplOperator.PLUS); }
                              | POWER                       { $op = new BinaryExpression.OperatorRef(getPosition($POWER          ),BinaryExpression.ImplOperator.POWER); }
                              | REM                         { $op = new BinaryExpression.OperatorRef(getPosition($REM            ),BinaryExpression.ImplOperator.REM); }
                              | TIMES                       { $op = new BinaryExpression.OperatorRef(getPosition($TIMES          ),BinaryExpression.ImplOperator.TIMES); }
                              | UNION                       { $op = new BinaryExpression.OperatorRef(getPosition($UNION          ),BinaryExpression.ImplOperator.UNION); }
                              | XOR                         { $op = new BinaryExpression.OperatorRef(getPosition($XOR            ),BinaryExpression.ImplOperator.XOR); }
                              ;

unaryExpression
returns [UnaryExpression exp]
                              :^( unaryOperator 
                                  expression
                                )                           {
                                                              $exp = UnaryExpression.create ( $unaryOperator.op, $expression.exp); 
                                                            }
                              ;

unaryOperator
returns [UnaryExpression.OperatorRef op]
                              : UNARY_PLUS                  { $op = new UnaryExpression.OperatorRef(getPosition($UNARY_PLUS ),UnaryExpression.ImplOperator.PLUS); }
                              | UNARY_MINUS                 { $op = new UnaryExpression.OperatorRef(getPosition($UNARY_MINUS),UnaryExpression.ImplOperator.MINUS); }
                              | NOT                         { $op = new UnaryExpression.OperatorRef(getPosition($NOT        ),UnaryExpression.ImplOperator.NOT); }
                              | ABS                         { $op = new UnaryExpression.OperatorRef(getPosition($ABS        ),UnaryExpression.ImplOperator.ABS); }
                              ;


rangeExpression
returns [MinMaxRange exp]
                              : ^( RANGE_DOTS
                                   from=expression
                                   to=expression
                                 )                          { $exp = new MinMaxRange ( $from.exp, $to.exp); }
                              ;




aggregateExpression
returns [StructureAggregate exp]
@init
{
  List<Expression> elements = new ArrayList<Expression>();
}
                              : ^( AGGREGATE
                                   ( expression             { elements.add($expression.exp); }
                                   )+ 
                                 )                          { $exp = new StructureAggregate(getPosition($AGGREGATE),elements); }
                              ;


linkExpression
returns [LinkUnlinkExpression exp]
                              : ^( linkExpressionType
                                   lhs=expression      
                                   relationshipSpec[$lhs.exp,false,false]
                                   rhs=expression?
                                 )                          { $exp = LinkUnlinkExpression.create ( $linkExpressionType.pos, 
                                                                                                   $linkExpressionType.type,
                                                                                                   $lhs.exp,
                                                                                                   $relationshipSpec.rel,
                                                                                                   $rhs.exp );
                                                            }
                              ;
linkExpressionType
returns [LinkUnlinkExpression.Type type, Position pos]
                              : LINK                        { $type =  LinkUnlinkExpression.LINK; $pos=getPosition($LINK); }
                              | UNLINK                      { $type =  LinkUnlinkExpression.UNLINK; $pos=getPosition($UNLINK); }
                              ;


navigateExpression
returns [NavigationExpression exp]
scope WhereClauseScope;
                              : ^( NAVIGATE
                                   expression
                                   relationshipSpec[$expression.exp,true,false]
                                                            { 
                                                              $WhereClauseScope::parentObject = $relationshipSpec.rel==null?null:$relationshipSpec.rel.getRelationshipSpec().getDestinationObject();
                                                            }
                                   ( whereClause           
                                   )?
                                 )                          { $exp = NavigationExpression.create(
                                                                                          getPosition($NAVIGATE),
                                                                                          $expression.exp,
                                                                                          $relationshipSpec.rel,
                                                                                          $whereClause.exp );
                                                            }
                                                                                          
                              ;

correlateExpression
returns [CorrelatedNavExpression exp]
                              : ^( CORRELATE
                                   lhs=expression
                                   rhs=expression
                                   relationshipSpec[$lhs.exp,true,true]
                                 )                          { $exp = CorrelatedNavExpression.create(
                                                                                          getPosition($CORRELATE),
                                                                                          $lhs.exp,
                                                                                          $rhs.exp,
                                                                                          $relationshipSpec.rel );
                                                            }
                              ;



orderByExpression
returns [OrderingExpression exp]
@init
{
  boolean reverse = false;
  List<OrderingExpression.OrderComponent> components = new ArrayList<OrderingExpression.OrderComponent>();
}
                              : ^( ( ORDERED_BY             { reverse = false; }
                                   | REVERSE_ORDERED_BY     { reverse = true; }
                                   ) 
                                   expression               
                                   ( sortOrder              { components.add(new OrderingExpression.OrderComponent($sortOrder.component,$sortOrder.reverse)); }
                                   )* 
                                 )                          { $exp = OrderingExpression.create ( getPosition($ORDERED_BY,$REVERSE_ORDERED_BY),$expression.exp,reverse,components); }
                              ;

sortOrder
returns [String component, boolean reverse]
                              : ^( SORT_ORDER_COMPONENT
                                   REVERSE?
                                   identifier               { $component = $identifier.name; $reverse = $REVERSE!=null; }
                                 )
                              ;

createExpression
returns [CreateExpression exp]
@init
{
  List<CreateExpression.CreateAggregateValue> args = new ArrayList<CreateExpression.CreateAggregateValue>();
}
                              : ^( CREATE
                                   objectReference 
                                   ( createArgument[$objectReference.ref]         { args.add($createArgument.arg); }
                                   )*
                                 )                          { $exp = CreateExpression.create (getPosition($CREATE),$objectReference.ref,args); }
                              ;

createArgument [ObjectNameExpression object]
returns [CreateExpression.CreateAggregateValue arg]
                              : ^( CREATE_ARGUMENT
                                   attributeName
                                   expression )             { $arg = CreateExpression.NormalAttribute.create(object,$attributeName.name,$expression.exp); } 
                              | ^( CURRENT_STATE
                                   stateName )              { $arg = CreateExpression.CurrentState.create(object,$stateName.name); } 
                              ;

findExpression
returns [FindExpression exp]
scope WhereClauseScope;
                              : ^( findType
                                   expression               { $WhereClauseScope::parentObject = FindExpression.getObject($expression.exp); }
                                   whereClause
                                 )                          { $exp = FindExpression.create ( $findType.pos, $findType.type, $expression.exp, $whereClause.exp ); }
                              ;

whereClause
returns [Expression exp]
                              : ^( WHERE
                                   ( expression             { $whereClause.exp = $expression.exp; }
                                   )?
                                 )
                              ;

findType
returns [ FindExpression.ImplType type, Position pos ]
                              : FIND                        { $findType.pos = getPosition($FIND);      $findType.type = FindExpression.ImplType.FIND; }
                              | FIND_ONE                    { $findType.pos = getPosition($FIND_ONE);  $findType.type = FindExpression.ImplType.FIND_ONE; }
                              | FIND_ONLY                   { $findType.pos = getPosition($FIND_ONLY); $findType.type = FindExpression.ImplType.FIND_ONLY; }
                              ;


dotExpression
returns [Expression exp]
                              : ^( DOT
                                   expression
                                   identifier
                                 )                          { $exp = DotExpression.create ( getPosition($identifier.name), $expression.exp, $identifier.name ); }
                              ;

terminatorServiceExpression
returns [Expression exp]
                              : ^( TERMINATOR_SCOPE
                                   expression
                                   identifier
                                 )                          { $exp = DomainTerminator.createServiceExpression ( getPosition($identifier.name), $expression.exp, $identifier.name ); }
                              ;

callExpression
returns [Expression exp]
@init
{
  List<Expression> args = new ArrayList<Expression>();
}
                              : ^( CALL
                                   expression               
                                   ( argument               { args.add($argument.arg); }
                                   )*                       
                                 )                          { $exp = CallExpression.create ( getPosition($CALL), $expression.exp, args ); }

                              ;

nameExpression
returns [Expression exp]      
                              : ^( NAME
                                   identifier
                                 )                          { $exp = resolveName ( $identifier.name ); }      
                              | ^( NAME
                                   domainReference
                                   identifier
                                 )                          { $exp = Name.create ( $domainReference.ref, $identifier.name); }
                              | ^( FIND_ATTRIBUTE
                                   identifier )             { $exp = FindAttributeNameExpression.create ( $WhereClauseScope::parentObject, $identifier.name); }
                              | compoundTypeName            { $exp = new TypeNameExpression($compoundTypeName.type.getPosition(),$compoundTypeName.type); }
                              ;


compoundTypeName
returns [BasicType type]      : instanceTypeRef             { $type = $instanceTypeRef.type; }
                              | sequenceTypeRef             { $type = $sequenceTypeRef.type; }
                              | arrayTypeRef                { $type = $arrayTypeRef.type; }
                              | setTypeRef                  { $type = $setTypeRef.type; }
                              | bagTypeRef                  { $type = $bagTypeRef.type; }
                              ;


argument
returns [Expression arg]
                              : ^( ARGUMENT
                                   expression
                                 )                          { $arg = $expression.exp; }
                              ;

sliceExpression
returns [Expression exp]
                              : ^( SLICE
                                   prefix=expression
                                   slice=expression
                                 )                          { $exp = SliceExpression.create (getPosition($SLICE),$prefix.exp,$slice.exp); }
                              ;

primeExpression
returns [Expression exp]
@init
{
  List<Expression> args = new ArrayList<Expression>();
}
                              : ^( PRIME
	                                 expression
                                   identifier
                                   ( argument               { args.add($argument.arg); }
                                   )*                       
                                 )                          { $exp = CharacteristicExpression.create(getPosition($PRIME),$expression.exp,$identifier.name, args); }
                              ;

literalExpression
returns [Expression exp]
                              : IntegerLiteral              { $exp = new IntegerLiteral(getPosition($IntegerLiteral),$IntegerLiteral.text);}
                              | RealLiteral                 { $exp = new RealLiteral(getPosition($RealLiteral),$RealLiteral.text);}
                              | CharacterLiteral            { $exp = org.xtuml.masl.metamodelImpl.expression.CharacterLiteral.create(getPosition($CharacterLiteral),$CharacterLiteral.text);}
                              | StringLiteral               { $exp = org.xtuml.masl.metamodelImpl.expression.StringLiteral.create(getPosition($StringLiteral),$StringLiteral.text);}
                              | TimestampLiteral            { $exp = org.xtuml.masl.metamodelImpl.expression.TimestampLiteral.create(getPosition($TimestampLiteral),$TimestampLiteral.text);}
                              | DurationLiteral             { $exp = org.xtuml.masl.metamodelImpl.expression.DurationLiteral.create(getPosition($DurationLiteral),$DurationLiteral.text);}
                              | TRUE                        { $exp = new BooleanLiteral(getPosition($TRUE),true);}
                              | FALSE                       { $exp = new BooleanLiteral(getPosition($FALSE),false);}
                              | NULL                        { $exp = new NullLiteral(getPosition($NULL)); }
                              | FLUSH                       { $exp = new FlushLiteral(getPosition($FLUSH));  }
                              | ENDL                        { $exp = new EndlLiteral(getPosition($ENDL));  }
                              | THIS                        { $exp = ThisLiteral.create(getPosition($THIS),currentService,currentState);  }
                              | CONSOLE                     { $exp = new ConsoleLiteral(getPosition($CONSOLE));  }
                              ; 
