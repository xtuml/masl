domain Example is
    type Struct is structure
        att1: integer;
        att2: integer;
    end structure;

    object Obj1;
    object Obj2;

    relationship R1 is Obj1 conditionally contains many Obj2, Obj2 conditionally contained_in one Obj1;

    public service svc1( a : in integer ); pragma idm_topic("test_topic_name");

    object Obj1 is
        id : preferred integer;

        event e1(a : in integer);
        state s1(a : in integer);
        state s2(a : in integer);

        transition is
            s1 ( e1 => s2 );
            s2 ( e1 => s1 );
        end transition;


    end object;

    object Obj2 is
        id : preferred integer;
        obj1_id : referential (R1.id) integer;
        s : Struct;
    end object;
end domain;
