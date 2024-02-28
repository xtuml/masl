package org.xtuml.masl.translate.kafka;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FunctionObjectCall;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.NewExpression;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.TypedefType;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.VariableDefinitionStatement;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.common.PragmaDefinition;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.DomainServiceTranslator;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.ParameterTranslator;
import org.xtuml.masl.translate.main.Types;


class ServiceTranslator
{

  private final DomainService service;
  private final DomainTranslator domainTranslator;
  private final Class handlerClass;
  private final Namespace domainNamespace;

  ServiceTranslator ( final DomainService service, final DomainTranslator domainTranslator )
  {
    this.service = service;
    this.domainTranslator = domainTranslator;
    domainNamespace = domainTranslator.getNamespace();
    handlerClass = new Class(Mangler.mangleName(service) + "Handler", domainNamespace);
  }

  void addServiceHandler (final CodeFile codeFile)
  {
    // create and add the handler class to the file
    handlerClass.addSuperclass(Kafka.serviceHandlerClass, Visibility.PUBLIC);
    final DeclarationGroup group = handlerClass.createDeclarationGroup();
    final Function getInvoker = handlerClass.createMemberFunction(group, "getInvoker", Visibility.PUBLIC);
    getInvoker.setReturnType(new TypeUsage(Kafka.callable));
    getInvoker.setConst(true);
    codeFile.addClassDeclaration(handlerClass);

    // create the invoker class
    final Class invokerClass = new Class(Mangler.mangleName(service) + "Invoker", domainNamespace);
    final DeclarationGroup functions = invokerClass.createDeclarationGroup();
    final Function constructor = invokerClass.createConstructor(functions, Visibility.PUBLIC);
    constructor.declareInClass(true);
    final Expression stream = constructor.createParameter(new TypeUsage(Kafka.bufferedInputStream, TypeUsage.Reference), "stream").asExpression();

    // create invoker function
    final Function invoker = invokerClass.createMemberFunction(functions, "operator()", Visibility.PUBLIC);
    invoker.declareInClass(true);

    // handle parameters
    final List<Expression> invokeArgs = new ArrayList<Expression>();
    final DeclarationGroup vars = invokerClass.createDeclarationGroup();
    for ( final ParameterDefinition param : service.getParameters() )
    {
      final TypeUsage type = Types.getInstance().getType(param.getType());
      final BasicType paramType = param.getType().getBasicType();
      if (!(paramType.getBasicType().getActualType() == ActualType.EVENT ||
           paramType.getBasicType().getActualType() == ActualType.DEVICE || paramType.getBasicType().getActualType() == ActualType.ANY_INSTANCE))
      {
        final Variable arg = invokerClass.createMemberVariable(vars, Mangler.mangleName(param), type, Visibility.PRIVATE);
        constructor.getCode().appendStatement(new BinaryExpression(stream, BinaryOperator.RIGHT_SHIFT, arg.asExpression()).asStatement());
        invokeArgs.add(arg.asExpression());
      }
      else
      {
        final Variable arg = new Variable(type, Mangler.mangleName(param));
        invoker.getCode().appendStatement(arg.asStatement());
        invokeArgs.add(arg.asExpression());
      }
    }

    // create the call to the service interceptor
    final DomainServiceTranslator serviceTranslator = DomainServiceTranslator.getInstance(service);
    final TypedefType serviceInterceptor = serviceTranslator.getServiceInterceptor();
    final Function serviceFunction = new Function("callService");
    final Expression instanceFnCall = serviceInterceptor.asClass().callStaticFunction("instance");
    Expression invokeExpression = new FunctionObjectCall(serviceFunction.asFunctionCall(instanceFnCall, false), invokeArgs);
    invoker.getCode().appendStatement(invokeExpression.asStatement());

    // add the invoker class to the file
    codeFile.addClassDeclaration(invokerClass);

    // add implementation of 'getInvoker' to the file
    final Expression stream2 = getInvoker.createParameter(new TypeUsage(Kafka.bufferedInputStream, TypeUsage.Reference), "stream").asExpression();
    getInvoker.getCode().appendStatement(new ReturnStatement(invokerClass.callConstructor(stream2)));
    codeFile.addFunctionDefinition(getInvoker);

  }

  void addTopicRegistration(final CodeFile codeFile)
  {
    final Expression processHandler = Kafka.processHandlerClass.callStaticFunction("getInstance");
    final Expression domainId = new Function("getId").asFunctionCall(new Function("getDomain").asFunctionCall(Architecture.process, false, Literal.createStringLiteral(domainTranslator.getDomain().getName())), false);
    final Expression serviceId = domainTranslator.getMainTranslator().getServiceTranslator(service).getServiceId();
    final Expression handler = Std.shared_ptr(new TypeUsage(handlerClass)).callConstructor(new NewExpression(new TypeUsage(handlerClass)));
    final Function registerServiceFunc = new Function("registerServiceHandler");
    final Expression registerService = registerServiceFunc.asFunctionCall(processHandler, false, domainId, serviceId, handler);
    final Variable registered = new Variable(new TypeUsage(FundamentalType.BOOL), Mangler.mangleName(service) + "_registered", new Namespace(""), registerService);
    codeFile.addVariableDefinition(registered);
  }

  void addPublisher(final CodeFile codeFile)
  {
    // create service function
    final Function function = new Function(Mangler.mangleName(service), domainNamespace);
    function.setReturnType(domainTranslator.getTypes().getType(service.getReturnType()));

    // create output buffer
    final Variable stream = new Variable(new TypeUsage(Kafka.bufferedOutputStream), "buffer");
    function.getCode().appendStatement(new VariableDefinitionStatement(stream));

    // create partition key buffer
    final Variable partKey = new Variable(new TypeUsage(Kafka.bufferedOutputStream), "part_key");
    function.getCode().appendStatement(new VariableDefinitionStatement(partKey));

    // handle parameters
    for ( final ParameterDefinition param : service.getParameters() )
    {
      final ParameterTranslator paramTrans = new ParameterTranslator(param, function);
      final Expression writeExpr = new BinaryExpression(stream.asExpression(), BinaryOperator.LEFT_SHIFT, paramTrans.getVariable().asExpression());
      function.getCode().appendStatement(new ExpressionStatement(writeExpr));

      if ( service.getDeclarationPragmas().hasPragma(DomainTranslator.KAFKA_PARTITION_KEY_PRAGMA) &&
          service.getDeclarationPragmas().getPragmaValues(DomainTranslator.KAFKA_PARTITION_KEY_PRAGMA).contains(param.getName()) )
      {
        final Expression keyWriteExpr = new BinaryExpression(partKey.asExpression(), BinaryOperator.LEFT_SHIFT, paramTrans.getVariable().asExpression());
        function.getCode().appendStatement(new ExpressionStatement(keyWriteExpr));
      }
    }

    // call publisher
    final Expression producer = Kafka.producerClass.callStaticFunction("getInstance");
    final Function publishFunc = new Function("publish");
    final Expression domainId = new Function("getId").asFunctionCall(new Function("getDomain").asFunctionCall(Architecture.process, false, Literal.createStringLiteral(domainTranslator.getDomain().getName())), false);
    final Expression serviceId = domainTranslator.getMainTranslator().getServiceTranslator(service).getServiceId();
    final Expression publishExpr = publishFunc.asFunctionCall(producer, false, domainId, serviceId, stream.asExpression(), partKey.asExpression());
    function.getCode().appendStatement(new ExpressionStatement(publishExpr));

    // add function to file
    codeFile.addFunctionDefinition(function);

    // add service registration
    final DomainServiceTranslator serviceTranslator = DomainServiceTranslator.getInstance(service);
    final TypedefType serviceInterceptor = serviceTranslator.getServiceInterceptor();
    final Expression interceptorFnCall = serviceInterceptor.asClass().callStaticFunction("instance");
    final Function registerFunction = new Function("registerLocal");
    final Expression initialValue = registerFunction.asFunctionCall(interceptorFnCall, false, function.asFunctionPointer());
    final Variable registrationVar = new Variable(new TypeUsage(FundamentalType.BOOL, TypeUsage.Const), "localServiceRegistration_" + Mangler.mangleName(service), domainNamespace, initialValue);
    registrationVar.setStatic(true);
    codeFile.addVariableDefinition(registrationVar);

  }

  void addCustomTopicName(final CodeFile codeFile) {
    if (service.getDeclarationPragmas().getPragmaValues(DomainTranslator.KAFKA_TOPIC_PRAGMA).size() == 1) {
      final String topicNameString = service.getDeclarationPragmas().getPragmaValues(DomainTranslator.KAFKA_TOPIC_PRAGMA).get(0);
      if (!isBoolean(topicNameString) && !isNumeric(topicNameString)) {
        final Expression processHandler = Kafka.processHandlerClass.callStaticFunction("getInstance");
        final Expression domainId = new Function("getId").asFunctionCall(new Function("getDomain").asFunctionCall(Architecture.process, false, Literal.createStringLiteral(domainTranslator.getDomain().getName())), false);
        final Expression serviceId = domainTranslator.getMainTranslator().getServiceTranslator(service).getServiceId();
        final Expression topicName = Literal.createStringLiteral(topicNameString);
        final Function setTopicNameFunc = new Function("setCustomTopicName");
        final Expression setTopicName = setTopicNameFunc.asFunctionCall(processHandler, false, domainId, serviceId, topicName);
        final Variable topicNameSet = new Variable(new TypeUsage(FundamentalType.BOOL), Mangler.mangleName(service) + "_topic_name_set", new Namespace(""), setTopicName);
        codeFile.addVariableDefinition(topicNameSet);
      }
    }
  }

  private boolean isBoolean(final String value) {
    return "true".equals(value) || "false".equals(value);
  }

  private boolean isNumeric(final String value) {
    try {
      double d = Double.parseDouble(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

}
