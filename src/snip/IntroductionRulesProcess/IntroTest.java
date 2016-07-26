package snip.IntroductionRulesProcess;

/*

This class is a test for the introduction process.

The main structure is creating a temporary SNePS environment, then calling the "BeginIntroduction" method from the "IntroductionRule" class, to check if the introduction process gives the same results required.

The process starts by creating all the required components for a new test environment, then for each logical connective in SNePS (e.g. And-Entailment, Or-Entailment, ...) it tests two things, firstly, if given a successful environment and rule if process succeeds to introduce the rule or not, and given a wrong enenvironmentr wrong rule if process fails to introduce the rule or not.

Note: Due to some problems occurring when retrieving all the reports from querying the system, the reports are all created customly to test, in order to simulate and test the introduction process.

 */

import SNeBR.SNeBR;
import sneps.CaseFrame;
import sneps.Network;
import sneps.Nodes.*;
import sneps.RCFP;
import sneps.Relation;
import sneps.SemanticClasses.Individual;
import SNeBR.Context;
import SNeBR.Support;
import sneps.match.LinearSubstitutions;
import snip.Channel;
import snip.Report;
import snip.Rules.RuleNodes.*;
import java.util.*;

public class IntroTest {

    private static VariableNode var1 = Network.buildVariableNode();
    private static VariableNode var2 = Network.buildVariableNode();

    private static ArrayList<Report> IntroReports = new ArrayList<Report>();
    private static ArrayList<Report> IntroReports2 = new ArrayList<Report>();
    private static ArrayList<Report> IntroReports3 = new ArrayList<Report>();
    private static ArrayList<Report> IntroReports4 = new ArrayList<Report>();
    private static ArrayList<Report> IntroReports5 = new ArrayList<Report>();
    private static ArrayList<Report> IntroReports6 = new ArrayList<Report>();
    private static ArrayList<Report> IntroReports7 = new ArrayList<Report>();


    public static ArrayList<Report> getIntroReports() {
        return IntroReports;
    }

    public static void setIntroReports(ArrayList<Report> introReports) {
        IntroReports = introReports;
    }

    public static ArrayList<Report> getIntroReports2() {
        return IntroReports2;
    }

    public static void setIntroReports2(ArrayList<Report> introReports2) {
        IntroReports2 = introReports2;
    }

    public static ArrayList<Report> getIntroReports3() {
        return IntroReports3;
    }

    public static void setIntroReports3(ArrayList<Report> introReports3) {
        IntroReports3 = introReports3;
    }

    public static ArrayList<Report> getIntroReports4() {
        return IntroReports4;
    }

    public static void setIntroReports4(ArrayList<Report> introReports4) {
        IntroReports4 = introReports4;
    }

    public static ArrayList<Report> getIntroReports5() {
        return IntroReports5;
    }

    public static void setIntroReports5(ArrayList<Report> introReports5) {
        IntroReports5 = introReports5;
    }

    public static ArrayList<Report> getIntroReports6() {
        return IntroReports6;
    }

    public static void setIntroReports6(ArrayList<Report> introReports6) {
        IntroReports6 = introReports6;
    }

    public static ArrayList<Report> getIntroReports7() {
        return IntroReports7;
    }

    public static void setIntroReports7(ArrayList<Report> introReports7) {
        IntroReports7 = introReports7;
    }



    public static void main(String[] args) throws Exception {


        CaseFrame.createRuleCaseFrame();

        Relation r1 = Network.defineRelation("member", "Individual", "none", 1);
//        System.out.println(r1.getName());

        Relation r2 = Network.defineRelation("class", "Individual", "none", 1);

        RCFP rp1 = Network.defineRelationPropertiesForCF(r1, "none", 1);

        RCFP rp2 = Network.defineRelationPropertiesForCF(r2, "none", 1);

        LinkedList<RCFP> relProperties = new LinkedList<RCFP>();

        relProperties.add(rp1);
        relProperties.add(rp2);

        CaseFrame cf1 = Network.defineCaseFrame("Proposition", relProperties);
        System.out.println(cf1.getSemanticClass().toString());

        Individual i = new Individual();
        Node b1 = null;
        try {
            b1 = Network.buildBaseNode("Mo", i);
        } catch (Exception exp) {
        }
        //

        Individual i1 = new Individual();
        Node b2 = null;
        try {
            b2 = Network.buildBaseNode("PhD", i1);
        } catch (Exception exp) {
        }


        Object[][] a1 = new Object[2][2];
        a1[0][0] = r1;
        a1[0][1] = b1;
        a1[1][0] = r2;
        a1[1][1] = b2;

        MolecularNode ant1 = null;
        try {
            ant1 = Network.buildMolecularNode(a1, cf1);
        } catch (Exception exp) {
        }
        System.out.println(ant1.getSemanticType() + " " + ant1);

        i = new Individual();
        b1 = null;
        try {
            b1 = Network.buildBaseNode("Mo", i);
        } catch (Exception exp) {
        }
        //
        i1 = new Individual();
        b2 = null;
        try {
            b2 = Network.buildBaseNode("Researcher", i1);
        } catch (Exception exp) {
        }

        a1 = new Object[2][2];
        a1[0][0] = r1;
        a1[0][1] = b1;
        a1[1][0] = r2;
        a1[1][1] = b2;

        MolecularNode ant2 = null;
        try {
            ant2 = Network.buildMolecularNode(a1, cf1);
        } catch (Exception exp) {
        }
        System.out.println(ant2.getSemanticType() + " " + ant2);

        i = new Individual();
        b1 = null;
        try {
            b1 = Network.buildBaseNode("Mo", i);
        } catch (Exception exp) {
        }
        //
        i1 = new Individual();
        b2 = null;
        try {
            b2 = Network.buildBaseNode("Academic", i1);
        } catch (Exception exp) {
        }

        a1 = new Object[2][2];
        a1[0][0] = r1;
        a1[0][1] = b1;
        a1[1][0] = r2;
        a1[1][1] = b2;

        MolecularNode ant3 = null;
        try {
            ant3 = Network.buildMolecularNode(a1, cf1);
        } catch (Exception exp) {
        }
        System.out.println(ant3.getSemanticType() + " " + ant3);

        i = new Individual();
        b1 = null;
        try {
            b1 = Network.buildBaseNode("Mo", i);
        } catch (Exception exp) {
        }
        //
        i1 = new Individual();
        b2 = null;
        try {
            b2 = Network.buildBaseNode("teacher", i1);
        } catch (Exception exp) {
        }

        a1 = new Object[2][2];
        a1[0][0] = r1;
        a1[0][1] = b1;
        a1[1][0] = r2;
        a1[1][1] = b2;

        MolecularNode ant4 = null;
        try {
            ant4 = Network.buildMolecularNode(a1, cf1);
        } catch (Exception exp) {
        }
        System.out.println(ant4.getSemanticType() + " " + ant4);


        i = new Individual();
        b1 = null;
        try {
            b1 = Network.buildBaseNode("Mo", i);
        } catch (Exception exp) {
        }
        //
        i1 = new Individual();
        b2 = null;
        try {
            b2 = Network.buildBaseNode("Student", i1);
        } catch (Exception exp) {
        }

        a1 = new Object[2][2];
        a1[0][0] = r1;
        a1[0][1] = b1;
        a1[1][0] = r2;
        a1[1][1] = b2;

        MolecularNode ant5 = null;
        try {
            ant5 = Network.buildMolecularNode(a1, cf1);
        } catch (Exception exp) {
        }
        System.out.println(ant5.getSemanticType() + " " + ant5);


        // building base nodes
        i = new Individual();
        b1 = null;
        try {
            b1 = Network.buildBaseNode("Mo", i);
        } catch (Exception exp) {
        }
        //
        i1 = new Individual();
        b2 = null;
        try {
            b2 = Network.buildBaseNode("Prof.", i1);
        } catch (Exception exp) {
        }

        a1 = new Object[2][2];
        a1[0][0] = r1;
        a1[0][1] = b1;
        a1[1][0] = r2;
        a1[1][1] = b2;

        MolecularNode cons = null;
        try {
            cons = Network.buildMolecularNode(a1, cf1);
        } catch (Exception exp) {
        }
        System.out.println(cons.getSemanticType() + " " + cons);

        a1 = new Object[3][2];
        a1[0][0] = Relation.andAnt;
        a1[0][1] = ant1;
        a1[1][0] = Relation.andAnt;
        a1[1][1] = ant2;
        a1[2][0] = Relation.cq;
        a1[2][1] = cons;
//        a1[3][0] = Relation.cq;
//        a1[3][1] = ant4;

        AndNode andRule = (AndNode)Network.buildMolecularNode(a1, CaseFrame.andRule);
        System.out.println(andRule);

        a1 = new Object[3][2];
        a1[0][0] = Relation.andAnt;
        a1[0][1] = ant3;
        a1[1][0] = Relation.andAnt;
        a1[1][1] = ant2;
        a1[2][0] = Relation.cq;
        a1[2][1] = cons;

        AndNode andRule2 = (AndNode)Network.buildMolecularNode(a1, CaseFrame.andRule);
        System.out.println(andRule2);

        a1 = new Object[3][2];
        a1[0][0] = Relation.andAnt;
        a1[0][1] = ant1;
        a1[1][0] = Relation.andAnt;
        a1[1][1] = ant2;
        a1[2][0] = Relation.cq;
        a1[2][1] = ant3;

        AndNode andRule3 = (AndNode)Network.buildMolecularNode(a1, CaseFrame.andRule);
        System.out.println(andRule3);

        a1 = new Object[3][2];
        a1[0][0] = Relation.andAnt;
        a1[0][1] = ant4;
        a1[1][0] = Relation.andAnt;
        a1[1][1] = ant2;
        a1[2][0] = Relation.cq;
        a1[2][1] = ant3;

        AndNode andRule4 = (AndNode)Network.buildMolecularNode(a1, CaseFrame.andRule);
        System.out.println(andRule4);

        a1 = new Object[3][2];
        a1[0][0] = Relation.ant;
        a1[0][1] = ant1;
        a1[1][0] = Relation.ant;
        a1[1][1] = ant4;
        a1[2][0] = Relation.cq;
        a1[2][1] = ant3;

        OrNode orRule = (OrNode)Network.buildMolecularNode(a1, CaseFrame.orRule);
        System.out.println(orRule);

        i = new Individual();
        Node b5 = null;
        try {
            b5 = Network.buildBaseNode("2", i);
        } catch (Exception exp) {
        }

        a1 = new Object[5][2];
        a1[0][0] = Relation.andAnt;
        a1[0][1] = ant1;
        a1[1][0] = Relation.andAnt;
        a1[1][1] = ant2;
        a1[2][0] = Relation.andAnt;
        a1[2][1] = ant3;
        a1[3][0] = Relation.cq;
        a1[3][1] = ant4;
        a1[4][0] = Relation.i;
        a1[4][1] = b5;


        NumericalNode numRule = (NumericalNode)Network.buildMolecularNode(a1, CaseFrame.numericalRule);
        System.out.println(numRule);

        a1 = new Object[3][2];
        a1[0][0] = Relation.andAnt;
        a1[0][1] = ant1;
        a1[1][0] = Relation.andAnt;
        a1[1][1] = ant2;
        a1[2][0] = Relation.cq;
        a1[2][1] = ant4;

        AndNode andRule5 = (AndNode)Network.buildMolecularNode(a1, CaseFrame.andRule);
        System.out.println(andRule5);

        a1 = new Object[3][2];
        a1[0][0] = Relation.andAnt;
        a1[0][1] = ant1;
        a1[1][0] = Relation.andAnt;
        a1[1][1] = ant3;
        a1[2][0] = Relation.cq;
        a1[2][1] = ant4;

        AndNode andRule6 = (AndNode)Network.buildMolecularNode(a1, CaseFrame.andRule);
        System.out.println(andRule6);

        a1 = new Object[3][2];
        a1[0][0] = Relation.andAnt;
        a1[0][1] = ant2;
        a1[1][0] = Relation.andAnt;
        a1[1][1] = ant3;
        a1[2][0] = Relation.cq;
        a1[2][1] = ant4;

        AndNode andRule7 = (AndNode)Network.buildMolecularNode(a1, CaseFrame.andRule);
        System.out.println(andRule7);

        i = new Individual();
        Node b6 = null;
        try {
            b6 = Network.buildBaseNode("4", i);
        } catch (Exception exp) {
        }

        a1 = new Object[7][2];
        a1[0][0] = Relation.arg;
        a1[0][1] = ant1;
        a1[1][0] = Relation.arg;
        a1[1][1] = ant2;
        a1[2][0] = Relation.arg;
        a1[2][1] = ant3;
        a1[3][0] = Relation.arg;
        a1[3][1] = ant4;
        a1[4][0] = Relation.arg;
        a1[4][1] = ant5;
        a1[5][0] = Relation.min;
        a1[5][1] = b5;
        a1[6][0] = Relation.max;
        a1[6][1] = b6;


        AndOrNode AndOrRule = (AndOrNode)Network.buildMolecularNode(a1, CaseFrame.andOrRule);
        System.out.println(AndOrRule);

        a1 = new Object[7][2];
        a1[0][0] = Relation.arg;
        a1[0][1] = ant1;
        a1[1][0] = Relation.arg;
        a1[1][1] = ant2;
        a1[2][0] = Relation.arg;
        a1[2][1] = ant3;
        a1[3][0] = Relation.arg;
        a1[3][1] = ant4;
        a1[4][0] = Relation.arg;
        a1[4][1] = ant5;
        a1[5][0] = Relation.threshMax;
        a1[5][1] = b6;
        a1[6][0] = Relation.thresh;
        a1[6][1] = b5;


        ThreshNode ThreshRule = (ThreshNode)Network.buildMolecularNode(a1, CaseFrame.threshRule);
        System.out.println(ThreshRule);

        System.out.println("=======================");
        System.out.println("Finished building Nodes");
        System.out.println("=======================");

        Context c = new Context(SNeBR.getCurrentContext());
        Context c1 = new Context(SNeBR.getCurrentContext());
        Context c2 = new Context(SNeBR.getCurrentContext());
        Context c3 = new Context(SNeBR.getCurrentContext());
        Context c4 = new Context(SNeBR.getCurrentContext());
        Context c5 = new Context(SNeBR.getCurrentContext());
        Context c6 = new Context(SNeBR.getCurrentContext());


//        =============================================
//        Start of And-Entailment test
//        =============================================

        SNeBR.setCurrentContext(c);

//        System.out.println(((Proposition)ant1.getSemantic()).isAsserted(SNeBR.getCurrentContext()));

        SNeBR.assertProposition((PropositionNode) andRule2);
        SNeBR.assertProposition((PropositionNode) orRule);

        Channel csqRule = new IntroductionChannel(new LinearSubstitutions(), new LinearSubstitutions(), SNeBR.getCurrentContext().getId(), cons, andRule, true);

        Support s1 = new Support();
        s1.addToOriginSet((PropositionNode) ant1);
        s1.addToOriginSet((PropositionNode) ant2);
        s1.addToOriginSet((PropositionNode) andRule);

        Support s2 = new Support();
        s2.addToOriginSet((PropositionNode) ant1);
        s2.addToOriginSet((PropositionNode) ant2);
        s2.addToOriginSet((PropositionNode) andRule2);
        s2.addToOriginSet((PropositionNode) orRule);


        Set<Support> sup = new HashSet<Support>();
        sup.add(s1);

        Report report1 = new Report(new LinearSubstitutions(), sup, true, c.getId());

        Set<Support> sup2 = new HashSet<Support>();
        sup2.add(s2);

        Report report2 = new Report(new LinearSubstitutions(), sup2, true, c.getId());


        ArrayList<Report> introrep = new ArrayList<Report>();
        introrep.add(report1);
        introrep.add(report2);

        setIntroReports(introrep);

//        System.out.println("------------------------------------");
//        IntroductionRule.BeginIntroduction(csqRule);

//        =============================================
//        Start of Or-Entailment test
//        =============================================

        SNeBR.setCurrentContext(c1);

        SNeBR.assertProposition((PropositionNode) ant2);
        SNeBR.assertProposition((PropositionNode) andRule3);
        SNeBR.assertProposition((PropositionNode) andRule4);


        Channel csqRule2 = new IntroductionChannel(new LinearSubstitutions(), new LinearSubstitutions(), SNeBR.getCurrentContext().getId(), ant3, orRule, true);


        Support s3 = new Support();
        s3.addToOriginSet((PropositionNode) ant2);
        s3.addToOriginSet((PropositionNode) ant4);
        s3.addToOriginSet((PropositionNode) andRule4);

        Support s4 = new Support();
        s4.addToOriginSet((PropositionNode) ant1);
        s4.addToOriginSet((PropositionNode) ant2);
        s4.addToOriginSet((PropositionNode) andRule3);

        Support s5 = new Support();
        s5.addToOriginSet((PropositionNode) ant1);
        s5.addToOriginSet((PropositionNode) orRule);


        Set<Support> sup3 = new HashSet<Support>();
        sup3.add(s3);

        Set<Support> sup4 = new HashSet<Support>();
        sup4.add(s4);

        Set<Support> sup5 = new HashSet<Support>();
        sup5.add(s5);

        Report report3 = new Report(new LinearSubstitutions(), sup3, true, c1.getId());

        Report report4 = new Report(new LinearSubstitutions(), sup4, true, c1.getId());

        Report report5 = new Report(new LinearSubstitutions(), sup5, true, c1.getId());



        ArrayList<Report> introrep2 = new ArrayList<Report>();
        introrep2.add(report3);
        introrep2.add(report4);
        introrep2.add(report5);
        setIntroReports2(introrep2);


//        System.out.println("------------------------------------");
//        IntroductionRule.BeginIntroduction(csqRule2);


//        =============================================
//        Start of Numerical-Entailment test
//        =============================================
//
        SNeBR.setCurrentContext(c2);

        SNeBR.assertProposition((PropositionNode) andRule5);
        SNeBR.assertProposition((PropositionNode) andRule6);
        SNeBR.assertProposition((PropositionNode) andRule7);


        Channel csqRule3 = new IntroductionChannel(new LinearSubstitutions(), new LinearSubstitutions(), SNeBR.getCurrentContext().getId(), ant4, numRule, true);

        Support s6 = new Support();
        s6.addToOriginSet((PropositionNode) ant1);
        s6.addToOriginSet((PropositionNode) ant2);
        s6.addToOriginSet((PropositionNode) numRule);

        Support s7 = new Support();
        s7.addToOriginSet((PropositionNode) ant1);
        s7.addToOriginSet((PropositionNode) ant2);
        s7.addToOriginSet((PropositionNode) andRule5);

        Support s8 = new Support();
        s8.addToOriginSet((PropositionNode) ant1);
        s8.addToOriginSet((PropositionNode) ant3);
        s7.addToOriginSet((PropositionNode) andRule6);

        Support s9 = new Support();
        s9.addToOriginSet((PropositionNode) ant2);
        s9.addToOriginSet((PropositionNode) ant3);
        s7.addToOriginSet((PropositionNode) andRule6);

        Set<Support> sup6 = new HashSet<Support>();
        sup6.add(s6);

        Set<Support> sup7 = new HashSet<Support>();
        sup7.add(s7);

        Set<Support> sup8 = new HashSet<Support>();
        sup8.add(s8);

        Set<Support> sup9 = new HashSet<Support>();
        sup9.add(s9);

        Report report6 = new Report(new LinearSubstitutions(), sup6, true, c2.getId());

        Report report7 = new Report(new LinearSubstitutions(), sup7, true, c2.getId());

        Report report8 = new Report(new LinearSubstitutions(), sup8, true, c2.getId());

        Report report9 = new Report(new LinearSubstitutions(), sup9, true, c2.getId());

        ArrayList<Report> introrep3 = new ArrayList<Report>();
        introrep3.add(report6);
        introrep3.add(report7);
        introrep3.add(report8);
        introrep3.add(report9);
        setIntroReports3(introrep3);

//        System.out.println("------------------------------------");
//        IntroductionRule.BeginIntroduction(csqRule3);

//        =============================================
//        Start of AndOr test
//        =============================================

        SNeBR.setCurrentContext(c3);

        SNeBR.assertProposition((PropositionNode) ant2);
        SNeBR.assertProposition((PropositionNode) ant3);

        Channel csqRule4 = new IntroductionChannel(new LinearSubstitutions(), new LinearSubstitutions(), SNeBR.getCurrentContext().getId(), ant1, AndOrRule, true);


        Support s10 = new Support();
        s10.addToOriginSet((PropositionNode) ant2);
        s10.addToOriginSet((PropositionNode) ant3);
        s10.addToOriginSet((PropositionNode) AndOrRule);

        Support s11 = new Support();
        s11.addToOriginSet((PropositionNode) ant2);
        s11.addToOriginSet((PropositionNode) ant3);

        Set<Support> sup10 = new HashSet<Support>();
        sup10.add(s10);

        Set<Support> sup11 = new HashSet<Support>();
        sup11.add(s11);

        Report report10 = new Report(new LinearSubstitutions(), sup10, true, c3.getId());

        Report report11 = new Report(new LinearSubstitutions(), sup11, true, c3.getId());

        ArrayList<Report> introrep4 = new ArrayList<Report>();
        introrep4.add(report10);
        introrep4.add(report11);
        setIntroReports4(introrep4);

//        System.out.println("------------------------------------");
//        IntroductionRule.BeginIntroduction(csqRule4);

//        =============================================
//        Start of Thresh test
//        =============================================

        SNeBR.setCurrentContext(c4);

        SNeBR.assertProposition((PropositionNode) ant1);

        Channel csqRule5 = new IntroductionChannel(new LinearSubstitutions(), new LinearSubstitutions(), SNeBR.getCurrentContext().getId(), ant1, ThreshRule, true);

        Support s12 = new Support();
        s12.addToOriginSet((PropositionNode) ant2);
        s12.addToOriginSet((PropositionNode) AndOrRule);

        Support s13 = new Support();
        s13.addToOriginSet((PropositionNode) ant3);

        Support s14 = new Support();
        s14.addToOriginSet((PropositionNode) ant1);
        s14.addToOriginSet((PropositionNode) ant2);
        s14.addToOriginSet((PropositionNode) ant3);
        s14.addToOriginSet((PropositionNode) ant4);
        s14.addToOriginSet((PropositionNode) ant5);

        Set<Support> sup12 = new HashSet<Support>();
        sup12.add(s12);

        Set<Support> sup13 = new HashSet<Support>();
        sup13.add(s13);

        Set<Support> sup14 = new HashSet<Support>();
        sup14.add(s14);

        Report report12 = new Report(new LinearSubstitutions(), sup12, true, c4.getId());

        Report report13 = new Report(new LinearSubstitutions(), sup13, true, c4.getId());

        Report report14 = new Report(new LinearSubstitutions(), sup14, true, c4.getId());

        ArrayList<Report> introrep5 = new ArrayList<Report>();
        introrep5.add(report12);
        introrep5.add(report13);
        introrep5.add(report14);
        setIntroReports5(introrep5);

//        System.out.println("------------------------------------");
//        IntroductionRule.BeginIntroduction(csqRule5);

//        =============================================
//        Start of Generic AndOr test
//        =============================================

        SNeBR.setCurrentContext(c5);

        SNeBR.assertProposition((PropositionNode) cons);

        Channel csqRule6 = new IntroductionChannel(new LinearSubstitutions(), new LinearSubstitutions(), SNeBR.getCurrentContext().getId(), ant1, AndOrRule, true);

        Support s15 = new Support();
        s15.addToOriginSet((PropositionNode) ant2);
        s15.addToOriginSet((PropositionNode) ant3);
        s15.addToOriginSet((PropositionNode) AndOrRule);

        Support s16 = new Support();
        s16.addToOriginSet((PropositionNode) ant1);
        s16.addToOriginSet((PropositionNode) ant2);
        s16.addToOriginSet((PropositionNode) ant3);
        s16.addToOriginSet((PropositionNode) ant4);

        Support s17 = new Support();
        s17.addToOriginSet((PropositionNode) ant1);
        s17.addToOriginSet((PropositionNode) ant2);
        s17.addToOriginSet((PropositionNode) ant3);
        s17.addToOriginSet((PropositionNode) ant5);

        Support s18 = new Support();
        s18.addToOriginSet((PropositionNode) ant1);
        s18.addToOriginSet((PropositionNode) ant3);
        s18.addToOriginSet((PropositionNode) ant4);
        s18.addToOriginSet((PropositionNode) ant5);

        Support s19 = new Support();
        s19.addToOriginSet((PropositionNode) ant1);
        s19.addToOriginSet((PropositionNode) ant2);
        s19.addToOriginSet((PropositionNode) ant4);
        s19.addToOriginSet((PropositionNode) ant5);

        Support s20 = new Support();
        s20.addToOriginSet((PropositionNode) ant2);
        s20.addToOriginSet((PropositionNode) ant3);
        s20.addToOriginSet((PropositionNode) ant4);
        s20.addToOriginSet((PropositionNode) ant5);


        Set<Support> sup15 = new HashSet<Support>();
        sup15.add(s15);

        Set<Support> sup16 = new HashSet<Support>();
        sup16.add(s16);

        Set<Support> sup17 = new HashSet<Support>();
        sup17.add(s17);

        Set<Support> sup18 = new HashSet<Support>();
        sup18.add(s18);

        Set<Support> sup19 = new HashSet<Support>();
        sup19.add(s19);

        Set<Support> sup20 = new HashSet<Support>();
        sup20.add(s20);



        Report report15 = new Report(new LinearSubstitutions(), sup15, true, c5.getId());
        Report report16 = new Report(new LinearSubstitutions(), sup16, true, c5.getId());
        Report report17 = new Report(new LinearSubstitutions(), sup17, true, c5.getId());
        Report report18 = new Report(new LinearSubstitutions(), sup18, true, c5.getId());
        Report report19 = new Report(new LinearSubstitutions(), sup19, true, c5.getId());
        Report report20 = new Report(new LinearSubstitutions(), sup20, true, c5.getId());


        ArrayList<Report> introrep6 = new ArrayList<Report>();
        introrep6.add(report15);
        introrep6.add(report16);
        introrep6.add(report17);
        introrep6.add(report18);
        introrep6.add(report19);
        introrep6.add(report20);
        setIntroReports6(introrep6);

        System.out.println("------------------------------------");
        IntroductionRule.BeginIntroduction(csqRule6);


//        =============================================
//        Start of Generic Thresh test
//        =============================================

        SNeBR.setCurrentContext(c6);

        SNeBR.assertProposition((PropositionNode) cons);

        Channel csqRule7 = new IntroductionChannel(new LinearSubstitutions(), new LinearSubstitutions(), SNeBR.getCurrentContext().getId(), ant1, ThreshRule, true);



        System.out.println("------------------------------------");
        IntroductionRule.BeginIntroduction(csqRule7);


    }


}








