package org.xtuml.masl.translate.kafka;

import java.util.Collections;
import java.util.List;

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

class TypeTranslator {

    private final TypeDeclaration type;
    private final DomainTranslator domainTranslator;
    private final CodeFile codeFile;

    private Function writer;
    private Function reader;

    TypeTranslator(TypeDeclaration type, DomainTranslator domainTranslator, CodeFile codeFile) {
        this.type = type;
        this.domainTranslator = domainTranslator;
        this.codeFile = codeFile;
    }

    List<Runnable> getFilePopulators() {
        if (writer != null && reader != null) {
            return List.of(() -> codeFile.addFunctionDeclaration(writer), () -> codeFile.addFunctionDeclaration(reader),
                    () -> codeFile.addFunctionDefinition(writer), () -> codeFile.addFunctionDefinition(reader));
        } else {
            return Collections.emptyList();
        }
    }

    void translate() {
        if (type.getVisibility() == org.xtuml.masl.metamodel.common.Visibility.PUBLIC) {
            if (type.getTypeDefinition() instanceof StructureType) {
                addStructureWriter((StructureType) type.getTypeDefinition());
                addStructureReader((StructureType) type.getTypeDefinition());
            } else if (type.getTypeDefinition() instanceof EnumerateType) {
                addEnumerationWriter((EnumerateType) type.getTypeDefinition());
                addEnumerationReader((EnumerateType) type.getTypeDefinition());
            }
        }
    }

    private void addStructureWriter(final StructureType struct) {
        final Structure structTrans = org.xtuml.masl.translate.main.DomainTranslator
                .getInstance(domainTranslator.getDomain()).getTypes()
                .getStructureTranslator(struct.getTypeDeclaration());
        final Class mainClass = structTrans.getMainClass();
        writer = Kafka.bufferedOutputStream.specialiseMemberFunction("write",
                TemplateSpecialisation.create(new TypeUsage(mainClass)));
        final Expression value = writer.createParameter(new TypeUsage(mainClass, TypeUsage.ConstReference), "value")
                .asExpression();
        for (final StructureElement element : struct.getElements()) {
            writer.getCode().appendStatement(writeElement(structTrans, element, value));
        }
    }

    private void addStructureReader(final StructureType struct) {
        final Structure structTrans = org.xtuml.masl.translate.main.DomainTranslator
                .getInstance(domainTranslator.getDomain()).getTypes()
                .getStructureTranslator(struct.getTypeDeclaration());
        final Class mainClass = structTrans.getMainClass();
        reader = Kafka.bufferedInputStream.specialiseMemberFunction("read",
                TemplateSpecialisation.create(new TypeUsage(mainClass)));
        final Expression value = reader.createParameter(new TypeUsage(mainClass, TypeUsage.Reference), "value")
                .asExpression();
        for (final StructureElement element : struct.getElements()) {
            reader.getCode().appendStatement(readElement(structTrans, element, value));
        }
    }

    private void addEnumerationWriter(final EnumerateType enumerate) {
        final EnumerationTranslator enumTrans = org.xtuml.masl.translate.main.DomainTranslator
                .getInstance(domainTranslator.getDomain()).getTypes()
                .getEnumerateTranslator(enumerate.getTypeDeclaration());
        final Class mainClass = enumTrans.getMainClass();
        writer = Kafka.bufferedOutputStream.specialiseMemberFunction("write",
                TemplateSpecialisation.create(new TypeUsage(mainClass)));
        final Expression value = writer.createParameter(new TypeUsage(mainClass, TypeUsage.ConstReference), "value")
                .asExpression();
        writer.getCode()
                .appendStatement(
                        new Function("write")
                                .asFunctionCall(Std.static_cast(new TypeUsage(FundamentalType.INT))
                                        .asFunctionCall(enumTrans.getGetIndex().asFunctionCall(value, false)))
                                .asStatement());
    }

    private void addEnumerationReader(final EnumerateType enumerate) {
        final EnumerationTranslator enumTrans = org.xtuml.masl.translate.main.DomainTranslator
                .getInstance(domainTranslator.getDomain()).getTypes()
                .getEnumerateTranslator(enumerate.getTypeDeclaration());
        final Class mainClass = enumTrans.getMainClass();
        reader = Kafka.bufferedInputStream.specialiseMemberFunction("read",
                TemplateSpecialisation.create(new TypeUsage(mainClass)));
        final Expression value = reader.createParameter(new TypeUsage(mainClass, TypeUsage.Reference), "value")
                .asExpression();
        final Variable index = new Variable(new TypeUsage(FundamentalType.INT), "index");
        reader.getCode().appendStatement(index.asStatement());
        reader.getCode().appendStatement(new Function("read").asFunctionCall(index.asExpression()).asStatement());
        final Expression newValue = enumTrans.getMainClass()
                .callConstructor(enumTrans.getIndexEnum().callConstructor(index.asExpression()));
        reader.getCode().appendExpression(new BinaryExpression(value, BinaryOperator.ASSIGN, newValue));
    }

    private Statement writeElement(final Structure structTrans, final StructureElement element,
            final Expression value) {
        final Function write = new Function("write");
        final Statement result = write.asFunctionCall(structTrans.getGetter(element).asFunctionCall(value, false))
                .asStatement();
        return result;
    }

    private Statement readElement(final Structure structTrans, final StructureElement element, final Expression value) {
        final Function read = new Function("read");
        final Statement result = read.asFunctionCall(structTrans.getSetter(element).asFunctionCall(value, false))
                .asStatement();
        return result;
    }

}
