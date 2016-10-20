/*
 * Filename : DomainTranslator.java
 * 
 * UK Crown Copyright (c) 2007. All Rights Reserved
 */
package org.xtuml.masl.translate.modeltimings;

import java.util.Arrays;
import java.util.Collection;

import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.main.DomainServiceTranslator;
import org.xtuml.masl.translate.main.TerminatorTranslator;
import org.xtuml.masl.translate.main.code.AssignmentTranslator;
import org.xtuml.masl.translate.main.code.CodeBlockTranslator;
import org.xtuml.masl.translate.main.code.CodeTranslator;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;
import org.xtuml.masl.translate.main.expression.FindTranslator;
import org.xtuml.masl.translate.main.object.ObjectTranslator;


/**
 * Define a class that can be used to instrument the architecture code with
 * additional code that can be used to give details on the timings of various
 * events during the execution of the model. For example the about of time spent
 * in a domain based service to the time spent during a find operation
 */
@Alias("ModelTimings")
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator
{

  public static DomainTranslator getInstance ( final Domain domain )
  {
    return getInstance(DomainTranslator.class, domain);
  }

  private DomainTranslator ( final Domain domain )
  {
    super(domain);
    mainDomainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
  }

  @Override
  public Collection<org.xtuml.masl.translate.DomainTranslator> getPrerequisites ()
  {
    return Arrays.<org.xtuml.masl.translate.DomainTranslator>asList(mainDomainTranslator);
  }

  @Override
  public void translate ()
  {
    instrumentDomainServices(domain);
    instrumentObjectServices(domain);
    instrumentTerminatorServices(domain);
  }

  private void instrumentFindServices ( final String methodName, final CodeTranslator translator )
  {
    if ( translator == null )
    {
      return;
    }

    if ( translator instanceof CodeBlockTranslator )
    {
      for ( final CodeTranslator child : translator.getChildTranslators() )
      {
        instrumentFindServices(methodName, child);
      }
    }
    else
    {
      if ( translator instanceof AssignmentTranslator )
      {
        final AssignmentTranslator assignment = (AssignmentTranslator)translator;
        final ExpressionTranslator rhs = assignment.getRhsTranslator();
        if ( rhs instanceof FindTranslator )
        {
          translator.getPreamble().appendStatement(TimingMonitor.getBeginTimingBlock("\"" + methodName
                                                                                     + " : find at line "
                                                                                     + translator.getMaslStatement()
                                                                                                 .getLineNumber()
                                                                                     + "\""));
          translator.getPostamble().appendStatement(TimingMonitor.getEndTimingBlock("\"" + methodName
                                                                                    + " : find at line "
                                                                                    + translator.getMaslStatement().getLineNumber()
                                                                                    + "\""));
        }
      }

      for ( final CodeTranslator child : translator.getChildTranslators() )
      {
        instrumentFindServices(methodName, child);
      }
    }
  }

  private void instrumentDomainServices ( final Domain domain )
  {
    for ( final DomainService service : domain.getServices() )
    {
      final DomainServiceTranslator serviceTranslator = mainDomainTranslator.getServiceTranslator(service);
      final CodeTranslator translator = serviceTranslator.getCodeTranslator();
      translator.getPreamble().appendStatement(TimingMonitor.getScopedTimingBlock(service.getFileName()));
      instrumentFindServices(service.getFileName(), translator);
    }
  }


  private void instrumentObjectServices ( final Domain domain )
  {
    for ( final ObjectDeclaration object : domain.getObjects() )
    {
      final ObjectTranslator objectTranslator = mainDomainTranslator.getObjectTranslator(object);
      for ( final ObjectService service : object.getServices() )
      {
        final CodeTranslator translator = objectTranslator.getServiceTranslator(service).getCodeTranslator();
        translator.getPreamble().appendStatement(TimingMonitor.getScopedTimingBlock(service.getFileName()));
        instrumentFindServices(service.getFileName(), translator);
      }
    }
  }

  private void instrumentTerminatorServices ( final Domain domain )
  {
    for ( final DomainTerminator term : domain.getTerminators() )
    {
      final TerminatorTranslator termTranslator = mainDomainTranslator.getTerminatorTranslator(term);
      for ( final DomainTerminatorService service : term.getServices() )
      {
        final CodeTranslator translator = termTranslator.getServiceTranslator(service).getCodeTranslator();
        translator.getPreamble().appendStatement(TimingMonitor.getScopedTimingBlock(service.getFileName()));
        instrumentFindServices(service.getFileName(), translator);
      }
    }
  }

  private final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator;
}
