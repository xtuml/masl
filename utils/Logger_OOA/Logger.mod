//
// UK Crown Copyright (c) 2009. All Rights Reserved
//

domain Logger is

  type Priority is enum ( Fatal, Error, Warning, Information, Debug, Trace );

  public service log         ( priority : in Priority, logger : in anonymous string,  message : in anonymous string );

  public service trace       ( logger : in anonymous string, message : in anonymous string );
  public service debug       ( logger : in anonymous string, message : in anonymous string );
  public service information ( logger : in anonymous string, message : in anonymous string );
  public service warning     ( logger : in anonymous string, message : in anonymous string );
  public service error       ( logger : in anonymous string, message : in anonymous string );
  public service fatal       ( logger : in anonymous string, message : in anonymous string );

  public service setLogLevel( logger : in anonymous string, priority : in Priority );

  public service enabled            ( priority : in Priority, logger : in anonymous string ) return boolean;
  public service traceEnabled       ( logger : in anonymous string ) return boolean;
  public service debugEnabled       ( logger : in anonymous string ) return boolean;
  public service informationEnabled ( logger : in anonymous string ) return boolean;
  public service warningEnabled     ( logger : in anonymous string ) return boolean;
  public service errorEnabled       ( logger : in anonymous string ) return boolean;
  public service fatalEnabled       ( logger : in anonymous string ) return boolean;

end domain;
pragma service_domain(true);

