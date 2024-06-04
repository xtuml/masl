package org.xtuml.masl.translate.kafka;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.xtuml.masl.cppgen.ArrayAccess;
import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Comment;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.VariableDefinitionStatement;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition.Mode;
import org.xtuml.masl.metamodel.common.Service;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.type.BooleanType;
import org.xtuml.masl.metamodelImpl.type.StringType;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.NlohmannJson;
import org.xtuml.masl.translate.main.ParameterTranslator;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;
import org.xtuml.masl.translate.main.Types;



@Alias("Kafka")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator
{

  public static final String KAFKA_TOPIC_PRAGMA = "kafka_topic";
  public static final String KAFKA_PARTITION_KEY_PRAGMA = "kafka_partition_key";

  private final org.xtuml.masl.translate.main.DomainTranslator mainTranslator;
  private final Namespace domainNamespace;
  private final Library library;
  private final Library interfaceLibrary;

  public static DomainTranslator getInstance ( final Domain domain )
  {
    return getInstance(DomainTranslator.class, domain);
  }

  private DomainTranslator ( final Domain domain )
  {
    super(domain);
    mainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
    domainNamespace = new Namespace(Mangler.mangleName(domain), Kafka.kafkaNamespace);


    library = new SharedLibrary(mainTranslator.getLibrary().getName() + "_kafka").inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
    library.addDependency(Kafka.library);
    library.addDependency(Kafka.cppkafkaLibrary);
    library.addDependency(Kafka.rdkafkaLibrary);

    interfaceLibrary = new SharedLibrary(mainTranslator.getLibrary().getName() + "_if_kafka").inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
    interfaceLibrary.addDependency(Kafka.library);
    interfaceLibrary.addDependency(Kafka.cppkafkaLibrary);
    interfaceLibrary.addDependency(Kafka.rdkafkaLibrary);
  }

  @Override
  public void translate ()
  {
    // create code files
    final CodeFile consumerCodeFile = library.createBodyFile("Kafka" + Mangler.mangleFile(domain));
    final CodeFile pollerCodeFile = library.createBodyFile("Kafka_pollers" + Mangler.mangleFile(domain));
    final CodeFile publisherCodeFile = interfaceLibrary.createBodyFile("Kafka_publishers" + Mangler.mangleFile(domain));

    // create service translators
    final List<ServiceTranslator> serviceTranslators = domain.getServices().stream()
      .filter(service -> service.getDeclarationPragmas().hasPragma(KAFKA_TOPIC_PRAGMA) && !service.isFunction() && !service.isExternal() && !service.isScenario())
      .map(service -> new ServiceTranslator(service, this)).collect(Collectors.toList());


    // translate service handlers
    for (final ServiceTranslator serviceTranslator : serviceTranslators)
    {
      serviceTranslator.addServiceHandler(consumerCodeFile);
    }

    // handle custom topics (consumer)
    for (final ServiceTranslator serviceTranslator : serviceTranslators)
    {
      serviceTranslator.addCustomTopicName(consumerCodeFile);
    }

    // add topic registrations
    for (final ServiceTranslator serviceTranslator : serviceTranslators)
    {
      serviceTranslator.addTopicRegistration(consumerCodeFile);
    }

    // create publisher services
    for (final ServiceTranslator serviceTranslator : serviceTranslators)
    {
      serviceTranslator.addPublisher(publisherCodeFile);
    }

    // handle custom topics (publisher)
    for (final ServiceTranslator serviceTranslator : serviceTranslators)
    {
      serviceTranslator.addCustomTopicName(publisherCodeFile);
    }
    
    // TODO temp
    final List<DomainTerminatorService> terminatorServices = domain.getTerminators().stream()
      .flatMap(terminator -> terminator.getServices().stream())
      .filter(service -> Optional.ofNullable(service.getReturnType()).map(t -> t.isAssignableFrom(BooleanType.createAnonymous())).orElse(false))
      .filter(service -> service.getDeclarationPragmas().hasPragma(KAFKA_TOPIC_PRAGMA))
      .collect(Collectors.toList());
    
    for (final DomainTerminatorService terminatorService : terminatorServices) {

    	// add service handlers
    	addServiceHandler2(pollerCodeFile, terminatorService);
    	
    	// add service overriders
    	
    	// add consumer initializers

    }
    

  }
  
  void addServiceHandler2 (final CodeFile codeFile, final DomainTerminatorService service)
  {

    // if the service has a single string parameter, just pass through without parsing JSON
    final boolean noParseJson = service.getParameters().size() == 1 && service.getParameters().get(0).getType().isAssignableFrom(StringType.createAnonymous());

    // create and add the consumer class to the file
    final Class consumerClass = new Class(Mangler.mangleName(service) + "Consumer", domainNamespace);
    consumerClass.addSuperclass(Kafka.dataConsumerClass, Visibility.PUBLIC);
    final DeclarationGroup functions = consumerClass.createDeclarationGroup();
    codeFile.addClassDeclaration(consumerClass);

    // create the constructor
    final Function constructor = consumerClass.createConstructor(functions, Visibility.PUBLIC);
    constructor.declareInClass(true);
    
    // create the accept function
    final Function acceptFn = consumerClass.createMemberFunction(functions, "accept", Visibility.PUBLIC);
    final Expression data = acceptFn.createParameter(new TypeUsage(Std.vector(new TypeUsage(Std.uint8))), "data").asExpression();
    acceptFn.setConst(true);
    final Predicate<BasicType> typeIsSerializable = paramType -> !(paramType.getBasicType().getActualType() == ActualType.EVENT ||
          paramType.getBasicType().getActualType() == ActualType.DEVICE || paramType.getBasicType().getActualType() == ActualType.ANY_INSTANCE);
    final Variable paramJson = new Variable(new TypeUsage(NlohmannJson.json), "params", NlohmannJson.parse(data));
    if (!noParseJson && service.getParameters().stream().map(p -> p.getType().getBasicType()).anyMatch(typeIsSerializable)) {
    	// Only build a JSON object if it will be used
        acceptFn.getCode().appendStatement(new VariableDefinitionStatement(paramJson));
    }
    
    // create the overrider function
        final Function overrider = new Function(Mangler.mangleName(service), domainNamespace);
        overrider.setReturnType(Types.getInstance().getType(service.getReturnType()));

        final List<Expression> consumerArgs = new ArrayList<>();
    final DeclarationGroup vars = consumerClass.createDeclarationGroup();
    for (final ParameterDefinition param : service.getParameters()) {
      final TypeUsage type = Types.getInstance().getType(param.getType());

      // add the parameter to the overrider function
      final ParameterTranslator paramTrans =  new ParameterTranslator(param, overrider);
      
      // only process "out" parameters
      if (param.getMode() == Mode.OUT) {
      
		  // capture each parameter as a member variable
		  final Variable constructorParam = constructor.createParameter(type.getReferenceType(), Mangler.mangleName(param));
		  final Variable memberVar = consumerClass.createMemberVariable(vars, Mangler.mangleName(param), type.getReferenceType(), Visibility.PRIVATE);
		  constructor.setInitialValue(memberVar, constructorParam.asExpression());
		  
		  // parse out each parameter and assign it to the member variable
		  if (typeIsSerializable.test(param.getType().getBasicType())) {
			Expression paramAccess = Std.string.callConstructor(new Function("begin").asFunctionCall(data, false), new Function("end").asFunctionCall(data, false));
			if (!noParseJson) {
			  paramAccess = NlohmannJson.get(service.getParameters().size() > 1 ? new ArrayAccess(paramJson.asExpression(), Literal.createStringLiteral(param.getName())) : paramJson.asExpression(), type);
			}
			acceptFn.getCode().appendStatement(new BinaryExpression(memberVar.asExpression(), BinaryOperator.ASSIGN, paramAccess).asStatement());
		  }
		  
		  // add to the list for the consumer call
		  consumerArgs.add(paramTrans.getVariable().asExpression());
      }
      
 
    }

    // create consumer instance
    final Variable consumer = new Variable(new TypeUsage(Kafka.consumerClass), "consumer_" + Mangler.mangleName(service), domainNamespace,
    		Kafka.consumerClass.callConstructor(Std.string.callConstructor(
    				getTopicName(service)
    				)));

    // add the call to consume to the overrider
    final Variable dataConsumer = new Variable(new TypeUsage(consumerClass), "dataConsumer", consumerClass.callConstructor(consumerArgs));
    overrider.getCode().appendStatement(
    		new VariableDefinitionStatement(dataConsumer));
    overrider.getCode().appendStatement(
    		new ReturnStatement(
    		new Function("consumeOne").asFunctionCall(consumer.asExpression(), false, 
    				dataConsumer.asExpression()
    				)));
    
    // add the accept function definition to the file
    codeFile.addFunctionDefinition(acceptFn);

    // add the consumer variable to fine file
    codeFile.addVariableDefinition(consumer);

    // add the overrider function definition to the file
    codeFile.addFunctionDefinition(overrider);

    // register the overrider function
    final Variable register = new Variable(new TypeUsage(FundamentalType.BOOL), "register_" + Mangler.mangleName(service), domainNamespace,
                             TerminatorServiceTranslator.getInstance(service).getRegisterOverride().asFunctionCall(overrider.asFunctionPointer()));
        codeFile.addVariableDefinition(register);
        

  }

  private Expression getTopicName(final DomainTerminatorService service) {
    if (service.getDeclarationPragmas().getPragmaValues(DomainTranslator.KAFKA_TOPIC_PRAGMA).size() == 1) {
      final String topicNameString = service.getDeclarationPragmas().getPragmaValues(DomainTranslator.KAFKA_TOPIC_PRAGMA).get(0);
      if (!isBoolean(topicNameString) && !isNumeric(topicNameString)) {
        return Literal.createStringLiteral(topicNameString);
      }
    }
	final Expression processHandler = Kafka.processHandlerClass.callStaticFunction("getInstance");
	final Expression domainId = new Function("getId").asFunctionCall(new Function("getDomain").asFunctionCall(Architecture.process, false, Literal.createStringLiteral(this.getDomain().getName())), false);
	final Expression serviceId = TerminatorServiceTranslator.getInstance(service).getServiceId();
	return new Function("getTopicName").asFunctionCall(processHandler, false, domainId, serviceId);
  }

  private boolean isBoolean(final String value) {
    return "true".equals(value) || "false".equals(value);
  }

  private boolean isNumeric(final String value) {
    try {
      Double.parseDouble(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }






  Namespace getNamespace()
  {
    return domainNamespace;
  }

  org.xtuml.masl.translate.main.DomainTranslator getMainTranslator()
  {
    return mainTranslator;
  }

}
