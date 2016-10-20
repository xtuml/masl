//
// File: TestCommand.java
//
// UK Crown Copyright (c) 2015. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.xtuml.masl.translate.cmake.language.arguments.SimpleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;
import org.xtuml.masl.translate.cmake.language.arguments.TaggedArgument;
import org.xtuml.masl.translate.cmake.language.commands.Command;

import com.google.common.collect.Lists;


public class TestCommand
{

  private static SimpleArgument toArg ( final String arg )
  {
    return new SingleArgument(arg);
  }

  private static List<SingleArgument> toArgList ( final String... args )
  {
    return Stream.of(args).map(SingleArgument::new).collect(Collectors.toList());
  }


  @Test
  public void testEmptyCommand () throws IOException
  {
    final StringWriter writer = new StringWriter();
    final Command command = new Command("some_command");
    command.writeCode(writer, ">>");
    Assert.assertEquals(">>some_command()\n", writer.toString());

  }

  @Test
  public void testSingleSimpleCommand () throws IOException
  {
    final StringWriter writer = new StringWriter();
    final Command command = new Command("some_command", toArgList("HELLO"));
    command.writeCode(writer, ">>");
    Assert.assertEquals(">>some_command ( HELLO )\n", writer.toString());

  }

  @Test
  public void testMultipleSimpleCommand () throws IOException
  {
    final StringWriter writer = new StringWriter();
    final Command command = new Command("some_command", toArgList("GOODBYE", "CRUEL", "WORLD"));
    command.writeCode(writer, ">>");
    Assert.assertEquals(">>some_command (\n>>  GOODBYE\n>>  CRUEL\n>>  WORLD\n>>  )\n", writer.toString());

  }

  @Test
  public void testSingleTaggedCommand () throws IOException
  {
    final StringWriter writer = new StringWriter();
    final Command command = new Command("some_command", new TaggedArgument(toArg("HELLO"), toArgList("GOODBYE", "CRUEL", "WORLD")));
    command.writeCode(writer, ">>");
    Assert.assertEquals(">>some_command (\n>>  HELLO \n>>        GOODBYE\n>>        CRUEL\n>>        WORLD\n>>  )\n",
                        writer.toString());

  }

  @Test
  public void testMultipleTaggedCommand () throws IOException
  {
    final StringWriter writer = new StringWriter();
    final Command command = new Command("some_command",
                                        Lists.newArrayList(
                                                           new TaggedArgument(toArg("HELLO"),
                                                                              toArgList("GOODBYE", "CRUEL", "WORLD")),
                                                           new TaggedArgument(toArg("HI"),
                                                                              toArgList("GOODBYE", "CRUEL", "WORLD"))));
    command.writeCode(writer, ">>");
    Assert.assertEquals(">>some_command (\n>>  HELLO \n>>        GOODBYE\n>>        CRUEL\n>>        WORLD\n>>  HI    \n>>        GOODBYE\n>>        CRUEL\n>>        WORLD\n>>  )\n",
                        writer.toString());

  }

}
