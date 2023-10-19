package org.xtuml.masl.translate.kafka;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TemplateSpecialisation;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.type.EnumerateType;
import org.xtuml.masl.metamodel.type.StructureElement;
import org.xtuml.masl.metamodel.type.StructureType;
import org.xtuml.masl.metamodel.type.TypeDeclaration;
import org.xtuml.masl.translate.main.EnumerationTranslator;
import org.xtuml.masl.translate.main.Structure;


class TypeTranslator
{

  private final TypeDeclaration type;
  private final DomainTranslator domainTranslator;

  TypeTranslator(final TypeDeclaration type, final DomainTranslator domainTranslator)
  {
    this.type = type;
    this.domainTranslator = domainTranslator;
  }

  void translate(final CodeFile codeFile)
  {
    if (type.getVisibility() == org.xtuml.masl.metamodel.common.Visibility.PUBLIC)
    {
      if ( type.getTypeDefinition() instanceof StructureType )
      {
        var writer = addStructureWriter((StructureType)type.getTypeDefinition());
        codeFile.addFunctionDeclaration(writer);
        codeFile.addFunctionDefinition(writer);
        Function reader = addStructureReader((StructureType) type.getTypeDefinition());
        codeFile.addFunctionDeclaration(reader);
        codeFile.addFunctionDefinition(reader);
      }
      else if ( type.getTypeDefinition() instanceof EnumerateType )
      {
        codeFile.addFunctionDefinition(addEnumerationWriter((EnumerateType)type.getTypeDefinition()));
        codeFile.addFunctionDefinition(addEnumerationReader((EnumerateType)type.getTypeDefinition()));
      }
    }
  }

  private Function addStructureWriter ( final StructureType struct )
  {
    final Structure structTrans = domainTranslator.getMainTranslator().getTypes().getStructureTranslator(struct.getTypeDeclaration());
    final Class mainClass = structTrans.getMainClass();
    final Function write = Kafka.bufferedOutputStream.specialiseMemberFunction("write", TemplateSpecialisation.create(new TypeUsage(mainClass)));
    final Expression value = write.createParameter(new TypeUsage(mainClass, TypeUsage.ConstReference), "value").asExpression();
    for ( final StructureElement element : struct.getElements() )
    {
      write.getCode().appendStatement(writeElement(structTrans, element, value));
    }
    return write;
  }

  private Function addStructureReader ( final StructureType struct )
  {
    final Structure structTrans =domainTranslator.getMainTranslator().getTypes().getStructureTranslator(struct.getTypeDeclaration());
    final Class mainClass = structTrans.getMainClass();
    final Function read = Kafka.bufferedInputStream.specialiseMemberFunction("read",TemplateSpecialisation.create(new TypeUsage(mainClass)));
    final Expression value = read.createParameter(new TypeUsage(mainClass, TypeUsage.Reference), "value").asExpression();
    for ( final StructureElement element : struct.getElements() )
    {
      read.getCode().appendStatement(readElement(structTrans, element, value));
    }
    return read;
  }

  private Function addEnumerationWriter ( final EnumerateType enumerate )
  {
    final EnumerationTranslator enumTrans =domainTranslator.getMainTranslator().getTypes().getEnumerateTranslator(enumerate.getTypeDeclaration());
    final Class mainClass = enumTrans.getMainClass();
    final Function write = Kafka.bufferedOutputStream.specialiseMemberFunction("write", TemplateSpecialisation.create(new TypeUsage(mainClass)));
    final Expression value = write.createParameter(new TypeUsage(mainClass, TypeUsage.ConstReference), "value").asExpression();
    write.getCode().appendStatement(new Function("write").asFunctionCall(Std.static_cast(new TypeUsage(FundamentalType.INT)).asFunctionCall(enumTrans.getGetIndex().asFunctionCall(value, false))).asStatement());
    return write;
  }

  private Function addEnumerationReader ( final EnumerateType enumerate )
  {
    final EnumerationTranslator enumTrans =domainTranslator.getMainTranslator().getTypes().getEnumerateTranslator(enumerate.getTypeDeclaration());
    final Class mainClass = enumTrans.getMainClass();
    final Function read = Kafka.bufferedInputStream.specialiseMemberFunction("read", TemplateSpecialisation.create(new TypeUsage(mainClass)));
    final Expression value = read.createParameter(new TypeUsage(mainClass, TypeUsage.Reference), "value").asExpression();
    final Variable index = new Variable(new TypeUsage(FundamentalType.INT), "index");
    read.getCode().appendStatement(index.asStatement());
    read.getCode().appendStatement(new Function("read").asFunctionCall(index.asExpression()).asStatement());
    final Expression newValue = enumTrans.getMainClass().callConstructor(enumTrans.getIndexEnum().callConstructor(index.asExpression()));
    read.getCode().appendExpression(new BinaryExpression(value, BinaryOperator.ASSIGN, newValue));
    return read;
  }

  private Statement writeElement ( final Structure structTrans, final StructureElement element, final Expression value )
  {
    final Function write = new Function("write");
    final Statement result = write.asFunctionCall(structTrans.getGetter(element).asFunctionCall(value, false)).asStatement();
    return result;
  }

  private Statement readElement ( final Structure structTrans, final StructureElement element, final Expression value )
  {
    final Function read = new Function("read");
    final Statement result = read.asFunctionCall(structTrans.getSetter(element).asFunctionCall(value, false)).asStatement();
    return result;
  }

}
