package com.cs407.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ProfessorSelect extends AppCompatActivity {
    // Currently Empty - Will get passed the URL for a mad grades website

    // See fetchURL in MainActivity of guidance on how to parse

    // Example:
    // URL: https://api.madgrades.com/v1/courses/3b14a2ff-b999-338f-b2a8-65b95ce52923

    //{"uuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923","number":407,"name":"Foundations of Mobile Systems and Applications",
    //"names":["Found of Mobl Systms\u0026Applctns"],
    // "subjects":[{"name":"Computer Sciences","abbreviation":"COMP SCI","code":"266"}],
    // "url":"https://api.madgrades.com/v1/courses/3b14a2ff-b999-338f-b2a8-65b95ce52923",
    // "gradesUrl":"https://api.madgrades.com/v1/courses/3b14a2ff-b999-338f-b2a8-65b95ce52923/grades",
    // "courseOfferings":[{"uuid":"297e78e9-cfc3-3e26-9740-36c63e3be950","courseUuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923",
    // "termCode":1222,"name":"Found of Mobl Systms\u0026Applctns","url":"https://api.madgrades.com/v1/course_offerings/297e78e9-cfc3-3e26-9740-36c63e3be950"},
    // {"uuid":"6930688a-4bbf-3212-adbb-b99b5cdc86db","courseUuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923","termCode":1204,"name":"Found of Mobl Systms\u0026Applctns","url":"https://api.madgrades.com/v1/course_offerings/6930688a-4bbf-3212-adbb-b99b5cdc86db"},
    // {"uuid":"5be924d0-6308-3062-98c4-136b16944a66","courseUuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923","termCode":1174,"name":"Found of Mobl Systms\u0026Applctns","url":"https://api.madgrades.com/v1/course_offerings/5be924d0-6308-3062-98c4-136b16944a66"},
    // {"uuid":"089fc737-6209-30dc-b8eb-3d3141ac89e9","courseUuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923","termCode":1164,"name":"Found of Mobl Systms\u0026Applctns","url":"https://api.madgrades.com/v1/course_offerings/089fc737-6209-30dc-b8eb-3d3141ac89e9"}]}

    // Of these, gradesUrl is probably the most important to us
    // Following that link will give us something like this:

    // {"courseUuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923",
    // "cumulative":{"total":882,"aCount":400,"abCount":262,"bCount":141,"bcCount":43,
    // "cCount":7,"dCount":10,"fCount":2,"sCount":15,"uCount":2,"crCount":0,"nCount":0,"pCount":0,
    // "iCount":0,"nwCount":0,"nrCount":0,"otherCount":0},
    // "courseOfferings":[{"termCode":1222,"cumulative":{"total":196,"aCount":52,"abCount":62,"bCount":47,"bcCount":33,"cCount":0,"dCount":2,
    // "fCount":0,"sCount":0,"uCount":0,"crCount":0,"nCount":0,"pCount":0,"iCount":0,"nwCount":0,"nrCount":0,"otherCount":0},
    // "sections":[{"sectionNumber":1,"instructors":[{"id":6294901,"name":"X / KEATON LEPPANEN"},{"id":6248778,"name":"X / MOHIT LOGANATHAN"},
    // {"id":3661702,"name":"SUMAN BANERJEE"},{"id":6249338,"name":"X / ISHA PADMANABAN"},{"id":6375575,"name":"X / MUHAMMAD HARIS NOOR"}],
    // "total":196,"aCount":52,"abCount":62,"bCount":47,"bcCount":33,"cCount":0,"dCount":2,"fCount":0,"sCount":0,"uCount":0,"crCount":0,"nCount":0,"pCount":0,"iCount":0,"nwCount":0,"nrCount":0,"otherCount":0}]},



    // Note: If it seems easier to parse the default webpage, we can switch to the normal webpage
    // by removing the api. and the v1/
    // https://api.madgrades.com/v1/courses/3b14a2ff-b999-338f-b2a8-65b95ce52923
    // ->
    // https://madgrades.com/courses/3b14a2ff-b999-338f-b2a8-65b95ce52923


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_select);
    }
}