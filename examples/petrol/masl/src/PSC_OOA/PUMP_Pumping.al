//
// UK Crown Copyright (c) 2016. All rights reserved.
//

state PSC::PUMP.Pumping () is

begin
  //# Engage clutch which starts pumping. 
  //# Continue until the gun trigger is released
  
  CLUTCH~>Engage_Clutch();

  console << "Pumping" << endl;
  schedule this.fuel_timer generate DELIVERY.Fuel_Unit_Delivered() to this->R3 delay @PT1S@ delta @PT1S@;

  end state;
