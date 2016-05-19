package snip.IntroductionRulesProcess;


import SNeBR.SNeBR;
import com.sun.org.apache.regexp.internal.RE;
import sneps.Nodes.*;
import SNeBR.Context;
import SNeBR.Support;
import sneps.SemanticClasses.Proposition;
import snip.Channel;
import snip.Report;
import snip.Rules.RuleNodes.AndOrNode;
import snip.Rules.RuleNodes.NumericalNode;
import snip.Rules.RuleNodes.ThreshNode;

import java.util.*;

public class IntroductionRule {


    public static void BeginIntroduction(Channel channel){

        Node Node = channel.getReporter();
        int ContextID = channel.getContextID();


        if (CanBeIntroduced((PropositionNode) Node, channel)) {

            if (Node.getClass().getName().contains("AndOrNode")) {
                System.out.println("This is AndOr node : " + Node + " Starting introduction");
                AndOrRuleHandler((PropositionNode) Node, ContextID);
//                GenericAndOrRuleHandler((PropositionNode) Node, ContextID);
            } else if (Node.getClass().getName().contains("ThreshNode")) {
                System.out.println("This is Thresh node : " + Node + " Starting introduction");
                ThreshRuleHandler((PropositionNode) Node, ContextID);
//                GenericThreshRuleHandler((PropositionNode) Node, ContextID);
            } else if (Node.getClass().getName().contains("AndNode")) {
                System.out.println("This is And Entailment node : " + Node + " Starting introduction");
                AndEntRuleHandler((PropositionNode) Node, ContextID);
            } else if (Node.getClass().getName().contains("OrNode")) {
                System.out.println("This is Or Entailment node : " + Node + " Starting introduction");
                OrEntRuleHandler((PropositionNode) Node, ContextID);
            } else if (Node.getClass().getName().contains("NumericalNode")) {
                System.out.println("This is Numerical node : " + Node + " Starting introduction");
                NumericalRuleHandler((PropositionNode) Node, ContextID);
            } else {
                System.out.println("This is not a valid Rule \"" + Node.toString() + "\"d");
            }

        } else {

            System.out.println("Failed to Introduce \"" + Node.toString() + "\"");

        }

        SNeBR.setCurrentContext(SNeBR.getContextByID(ContextID));

        System.out.println("Finished introduction");
        System.out.println("=======================");

    }

    private static boolean CanBeIntroduced(PropositionNode Node, Channel channel){
        System.out.println("Can start introduction");
        System.out.println("===========================");

        if (Node.alreadyWorking(channel)){
            System.out.println("Fail : already working on this channel");
            return false;
        }

        if (((Proposition)Node.getSemantic()).isAsserted(SNeBR.getContextByID(channel.getContextID()))){
            System.out.println("Fail : rule is already asserted");
            return false;
        }

        if (!Node.getClass().getName().contains("RuleNodes")){
            System.out.println("Fail : can not be introduced : Not a RuleNode \"" + Node.toString() + "\"");
            return false;
        }

        if (Node.getSyntacticType().contains("Pattern")){
//            match.ApplySubstitutions();
            System.out.println("Fail : can not be introduced : Not a closed Rule (Pattern Node with variables) \""  + Node.toString() + "\"");
            return false;
        }

        System.out.println("Yes Can be introduced, Not already working on, Not asserted, Not a pattern node, is a RuleNode ");
        System.out.println("");
        return true;
    }

    private static void AndEntRuleHandler(PropositionNode AndNode, int ContextID){
        System.out.println("And Entailment Handler");
        System.out.println("===========================");

        ArrayList<Node> antecedents = new ArrayList<Node>();
        ArrayList<Node> consequences = new ArrayList<Node>();
        Context TempContext = new Context();
        int PosCount = 0;
        int NegCount = 0;
        boolean wrongrule = false;



        for(Node ant : AndNode.getDownCableSet().getDownCable("&ant").getNodeSet()){antecedents.add(ant);}
        System.out.println("");
        System.out.println("Antecedents of rule " + antecedents);
        for(Node con : AndNode.getDownCableSet().getDownCable("cq").getNodeSet()){consequences.add(con);}
        System.out.println("");
        System.out.println("Consequences of rule " + consequences);
        TempContext = getTempContext(ContextID, antecedents);
        System.out.println("");
        System.out.println("Temporary context of And Rule " + TempContext.getHypothesisSet().getPropositions());


        for(Node conseq : consequences){

            ArrayList<Report> reports = getConsequentReports(conseq, AndNode, TempContext);
            int isAndEntReportSetValid = isAndEntReportSetValid(reports, AndNode, antecedents);

            if (isAndEntReportSetValid > 1){
                wrongrule = true;
                break;
            } else if (isAndEntReportSetValid == -1){
                NegCount+=1;
                break;
            } else if (isAndEntReportSetValid == 1){
                PosCount+=1;
            }
        }

        ConcludeIntroduction(wrongrule, PosCount, NegCount, consequences.size());



    }

    private static int isAndEntReportSetValid(ArrayList<Report> reports, Node RuleNode, ArrayList<Node> antecedents){

        System.out.println("Is And Entailment report set valid");
        System.out.println("---------------------------");

        int isValid = 0;

        for(int i = 0; i < reports.size() ; i++) {
            System.out.println("Report " + i + " is :");
            reports.get(1).getSupports().forEach((sup) -> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));
        }
        System.out.println("");

        for (Report rep : reports){

            for (Support sup : rep.getSupports()){

                boolean containsIntroRule = sup.getOriginSet().getHypothesisSet().getPropositions().contains(RuleNode);
                boolean containsNodesReq = sup.getOriginSet().getHypothesisSet().getPropositions().containsAll(antecedents);

//                System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions().contains(RuleNode));
//                System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions().containsAll(NodesRequired));

                if (!containsIntroRule && !containsNodesReq){
                    System.out.println("Failed, supports do not depend on the antecedents of the rule");
                    return 3;
                }

                if (rep.getSign() == false && !containsIntroRule && containsNodesReq){
                    return -1;
                }

                if (rep.getSign() == true && !containsIntroRule && containsNodesReq){
                    System.out.println("Yes, does not contain RuleNode " + RuleNode);
                    System.out.println("Yes, has in supports " + antecedents);
                    isValid = 1;
                }
            }
        }

        return isValid;
    }

    private static void  OrEntRuleHandler(PropositionNode OrNode, int ContextID){
        System.out.println("Or Entailment Handler");
        System.out.println("===========================");

        ArrayList<Node> antecedents = new ArrayList<Node>();
        ArrayList<Node> consequences = new ArrayList<Node>();
        Context TempContext = new Context();
        int PosCount = 0;
        int NegCount = 0;
        boolean wrongRule = false;


        for(Node ant : OrNode.getDownCableSet().getDownCable("ant").getNodeSet()){antecedents.add(ant);}
        System.out.println("");
        System.out.println("Antecedents of rule " + antecedents);
        for(Node con : OrNode.getDownCableSet().getDownCable("cq").getNodeSet()){consequences.add(con);}
        System.out.println("");
        System.out.println("Consequences of rule " + consequences);
        TempContext = getTempContext(ContextID, antecedents);
        System.out.println("");
        System.out.println("Temporary context of Or Rule " + TempContext.getHypothesisSet().getPropositions());

        for(Node conseq : consequences){

            ArrayList<Report> reports = getConsequentReports(conseq, OrNode, TempContext);
            int isOrEntReportSetValid = isOrEntReportSetValid(reports, OrNode, antecedents);

            if (isOrEntReportSetValid > 1){
                wrongRule = true;
                break;
            } else if (isOrEntReportSetValid == -1){
                NegCount+=1;
                break;
            } else if (isOrEntReportSetValid == 1){
                PosCount+=1;
            }

        }


        ConcludeIntroduction(wrongRule, PosCount, NegCount, consequences.size());


    }

    private static int isOrEntReportSetValid(ArrayList<Report> reports, Node RuleNode, ArrayList<Node> antecedents){
        System.out.println("Is Or Entailment report set valid");
        System.out.println("---------------------------");

        ArrayList<Node> requiredNodes = new ArrayList<Node>(antecedents);
        int isValid = 0;

        for(int i = 0; i < reports.size() ; i++) {
            System.out.println("Report " + i + " is :");
            reports.get(1).getSupports().forEach((sup) -> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));
        }
        System.out.println("");

        for (Report rep : reports){
            for (Support sup : rep.getSupports()){

                boolean containsIntroRule = sup.getOriginSet().getHypothesisSet().getPropositions().contains(RuleNode);
                boolean containsNoAntecedent = Collections.disjoint(antecedents, sup.getOriginSet().getHypothesisSet().getPropositions());

                ArrayList<Node> commonNodes = new ArrayList<Node>(requiredNodes);
                commonNodes.retainAll(sup.getOriginSet().getHypothesisSet().getPropositions());

                if (!containsIntroRule && containsNoAntecedent) {
                    System.out.println("Failed, supports do not depend on the antecedents of the rule");
                    return 3;
                }

                if (rep.getSign() == false && !containsIntroRule && !requiredNodes.isEmpty() && commonNodes.size() == 1){
                    return -1;
                }

                if (rep.getSign() == true && !containsIntroRule && !requiredNodes.isEmpty() && commonNodes.size() == 1){
                    requiredNodes.remove(commonNodes.get(0));
                }
            }
        }

        if (requiredNodes.isEmpty()){
            isValid = 1;
        }

        return isValid;
    }

    private static void NumericalRuleHandler(PropositionNode NumericalNode, int ContextID) {
        System.out.println("Numerical Handler");
        System.out.println("===========================");

        ArrayList<Node> antecedents = new ArrayList<Node>();
        ArrayList<Node> consequences = new ArrayList<Node>();
        Context TempContext = new Context();
        int PosCount = 0;
        int NegCount = 0;
        boolean wrongRule = false;


        for(Node ant : NumericalNode.getDownCableSet().getDownCable("&ant").getNodeSet()){antecedents.add(ant);}
        System.out.println("");
        System.out.println("Antecedents of rule " + antecedents);
        for(Node con : NumericalNode.getDownCableSet().getDownCable("cq").getNodeSet()){consequences.add(con);}
        System.out.println("");
        System.out.println("Consequences of rule " + consequences);
        TempContext = getTempContext(ContextID, antecedents);
        System.out.println("");
        System.out.println("Temporary context of Or Rule " + TempContext.getHypothesisSet().getPropositions());

        for(Node conseq : consequences){

            ArrayList<Report> reports = getConsequentReports(conseq, NumericalNode, TempContext);
            int isNumEntReportSetValid = isNumEntReportSetValid(reports, NumericalNode, antecedents);

            if (isNumEntReportSetValid > 1){
                wrongRule = true;
                break;
            } else if (isNumEntReportSetValid == -1){
                NegCount+=1;
                break;
            } else if (isNumEntReportSetValid == 1){
                PosCount+=1;
            }

        }

        ConcludeIntroduction(wrongRule, PosCount, NegCount, consequences.size());


    }

    private static int isNumEntReportSetValid(ArrayList<Report> reports, Node RuleNode, ArrayList<Node> antecedents){

        ArrayList<ArrayList<Node>> ReqNodes = Combinations(antecedents.size(), ((NumericalNode) RuleNode).getI(), antecedents);
        System.out.println("Permutations of Numerical Entailment");
        System.out.println(ReqNodes);
        System.out.println("------------------------------------");

        int isValid = 0;

        for (int i = 0; i < reports.size() ; i++) {
            System.out.println("Report " + i + " is :");
            reports.get(1).getSupports().forEach((sup) -> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));
        }
        System.out.println("");

        for (Report rep : reports){
            for (Support sup : rep.getSupports()){

                boolean containsIntroRule = sup.getOriginSet().getHypothesisSet().getPropositions().contains(RuleNode);
                boolean containsNoAntecedent = Collections.disjoint(antecedents, sup.getOriginSet().getHypothesisSet().getPropositions());
                ArrayList<ArrayList<Node>> commonNodes = new ArrayList<ArrayList<Node>>(ReqNodes);


                if (!containsIntroRule && containsNoAntecedent) {
                    System.out.println("Failed, supports do not depend on the antecedents of the rule");
                    return 3;
                }

                if (!containsIntroRule) {
                    for (ArrayList<Node> nodes : commonNodes) {
                        boolean containsNodesReq = sup.getOriginSet().getHypothesisSet().getPropositions().containsAll(nodes);

                        if (rep.getSign() == false && containsNodesReq){
                            return -1;
                        }

                        if (rep.getSign() == true && containsNodesReq) {
                            ReqNodes.remove(nodes);
                        }
                    }
                }
            }
        }

        if (ReqNodes.isEmpty()){
            isValid = 1;
        }

        return isValid;
    }

    private static void AndOrRuleHandler(PropositionNode AndOrNode, int ContextID) {
        System.out.println("AndOr Handler");
        System.out.println("===========================");

        ArrayList<Node> arguments = new ArrayList<Node>();
//        Context TempContext = new Context();

        for(Node ant : AndOrNode.getDownCableSet().getDownCable("arg").getNodeSet()){arguments.add(ant);}
        System.out.println("");
        System.out.println("Arguments of rule " + arguments);
//        TempContext = getTempContext(ContextID, arguments);
//        System.out.println("");
//        System.out.println("Temporary context of AndOr Rule " + TempContext.getHypothesisSet().getPropositions());


        ArrayList<Report> reports = getConsequentReports(AndOrNode, SNeBR.getContextByID(ContextID), -5);
        int isAndOrReportSetValid = isAndOrReportSetValid(reports, AndOrNode, arguments);

        if (isAndOrReportSetValid == -1){
            System.out.println("Rule is wrong and can not be introduced, but its negation can be introduced");
        } else if (isAndOrReportSetValid == 1){
            System.out.println("Rule is correct and can be introduced");
        } else {
            System.out.println("Failed, supports are either less that minimum requirement or more than max requirement");
        }


    }

    private static int isAndOrReportSetValid(ArrayList<Report> reports, Node RuleNode, ArrayList<Node> arguments){

        System.out.println("Is AndOr report set valid");
        System.out.println("---------------------------");

        int isValid = 4;

        for (int i = 0; i < reports.size() ; i++) {
            System.out.println("Report " + i + " is :");
            reports.get(1).getSupports().forEach((sup) -> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));
        }
        System.out.println("");


        for (Report rep : reports){
            for (Support sup : rep.getSupports()){

//                boolean containsIntroRule = sup.getOriginSet().getHypothesisSet().getPropositions().contains(RuleNode);
//                boolean containsNoArguments = Collections.disjoint(arguments, sup.getOriginSet().getHypothesisSet().getPropositions());

                ArrayList<Node> commonNodes = new ArrayList<Node>(arguments);
                commonNodes.retainAll(sup.getOriginSet().getHypothesisSet().getPropositions());


                if (commonNodes.size() < ((AndOrNode) RuleNode).getMin() || commonNodes.size() > ((AndOrNode) RuleNode).getMax()){
                    System.out.println("Failed, supports are either less that minimum requirement or more than max requirement");
                    return 4;
                }

                if (rep.getSign() == false && commonNodes.size() >= ((AndOrNode) RuleNode).getMin() && commonNodes.size() <= ((AndOrNode) RuleNode).getMax() ){
                    return -1;
                }

                if (rep.getSign() == true && commonNodes.size() >= ((AndOrNode) RuleNode).getMin() && commonNodes.size() <= ((AndOrNode) RuleNode).getMax() ){
                    isValid = 1;
                }

            }
        }


        return isValid;
    }

    private static void ThreshRuleHandler(PropositionNode ThreshNode, int ContextID) {
        System.out.println("Thresh Handler");
        System.out.println("===========================");


        ArrayList<Node> arguments = new ArrayList<Node>();
//        Context TempContext = new Context();

        for(Node ant : ThreshNode.getDownCableSet().getDownCable("arg").getNodeSet()){arguments.add(ant);}
        System.out.println("");
        System.out.println("Arguments of rule " + arguments);
//        TempContext = getTempContext(ContextID, arguments);
//        System.out.println("");
//        System.out.println("Temporary context of Thresh Rule " + TempContext.getHypothesisSet().getPropositions());


        ArrayList<Report> reports = getConsequentReports(ThreshNode, SNeBR.getContextByID(ContextID), -5);
        int isThreshReportSetValid = isThreshReportSetValid(reports, ThreshNode, arguments);

        if (isThreshReportSetValid == -1){
            System.out.println("Rule is wrong and can not be introduced, but its negation can be introduced");
        } else if (isThreshReportSetValid == 1){
            System.out.println("Rule is correct and can be introduced");
        } else {
            System.out.println("Failed, supports are more than the minimum and less than the maximum");
        }


    }

    private static int isThreshReportSetValid(ArrayList<Report> reports, Node RuleNode, ArrayList<Node> arguments){

        System.out.println("Is Thresh report set valid");
        System.out.println("---------------------------");

        int isValid = 0;

        for (int i = 0; i < reports.size() ; i++) {
             System.out.println("Report " + i + " is :");
             reports.get(1).getSupports().forEach((sup) -> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));
        }
        System.out.println("");

        for (Report rep : reports){
            for (Support sup : rep.getSupports()){

//                boolean containsIntroRule = sup.getOriginSet().getHypothesisSet().getPropositions().contains(RuleNode);
//                boolean containsNoArguments = Collections.disjoint(arguments, sup.getOriginSet().getHypothesisSet().getPropositions());

                ArrayList<Node> commonNodes = new ArrayList<Node>(arguments);
                commonNodes.retainAll(sup.getOriginSet().getHypothesisSet().getPropositions());


                if (commonNodes.size() >= ((ThreshNode) RuleNode).getThresh() && commonNodes.size() <= ((ThreshNode) RuleNode).getThreshMax()){
                    System.out.println("Failed, supports are more than minimum and less than max");
                    return 4;
                }

                if (rep.getSign() == false && (commonNodes.size() < ((ThreshNode) RuleNode).getThresh() || commonNodes.size() > ((ThreshNode) RuleNode).getThreshMax()) ){
                    return -1;
                }

                if (rep.getSign() == true && (commonNodes.size() < ((ThreshNode) RuleNode).getThresh() || commonNodes.size() > ((ThreshNode) RuleNode).getThreshMax()) ){
                    isValid = 1;
                }

            }
        }


        return isValid;
    }

    private static void GenericAndOrRuleHandler(PropositionNode AndOrNode, int ContextID){

        System.out.println("Generic AndOr Handler");
        System.out.println("===========================");

        ArrayList<Node> arguments = new ArrayList<Node>();
        ArrayList<Context> TempContexts = new ArrayList<Context>();
        int contextNum = 0;
        boolean isNeg = false;
        boolean isWrong = false;

        for(Node ant : AndOrNode.getDownCableSet().getDownCable("arg").getNodeSet()){arguments.add(ant);}
        System.out.println("");
        System.out.println("Arguments of rule " + arguments);

        ArrayList<ArrayList<Node>> ReqNodes1 = Combinations(arguments.size(), ((AndOrNode) AndOrNode).getMax(), arguments);

        ArrayList<ArrayList<Node>> ReqNodes2 = Combinations(arguments.size(), ( arguments.size() - ((AndOrNode) AndOrNode).getMin() ) + 1, arguments);

        HashSet<ArrayList<Node>> ReqNodesUni = new HashSet<ArrayList<Node>>();
        ReqNodesUni.addAll(ReqNodes1);
        ReqNodesUni.addAll(ReqNodes2);

        ArrayList<ArrayList<Node>> ReqNodes = new ArrayList<ArrayList<Node>>();
        ReqNodes.addAll(ReqNodesUni);

        TempContexts = GetGenericAndOrTempContext(arguments.size(), ((AndOrNode) AndOrNode).getMax(), arguments, ContextID, ReqNodes);

        for (int i = 0; i < TempContexts.size() ; i++){
            System.out.println(TempContexts.get(i).getHypothesisSet().getPropositions());

            ArrayList<Report> reports = getConsequentReports(AndOrNode, TempContexts.get(0), 5);
            int isGenericAndOrReportSetValid = isGenericAndOrReportSetValid(reports, AndOrNode, arguments, ReqNodes.get(i));

            if (isGenericAndOrReportSetValid == 3){
                System.out.println("Wrong rule, supports do not depend on the antecedents of the rule");
                isWrong = true;
                break;
            } else if (isGenericAndOrReportSetValid == 4){
                System.out.println("Failed, supports are either less that minimum requirement or more than max requirement");
                break;
            } else if (isGenericAndOrReportSetValid == -1){
                System.out.println("Rule is wrong and can not be introduced, but its negation can be introduced");
                isNeg = true;
                break;
            } else if (isGenericAndOrReportSetValid == 1){
                contextNum +=1;
            }
        }

        if (contextNum == TempContexts.size() && isNeg == false && isWrong == false){
            System.out.println("Rule is correct and can be introduced");
        } else if (isNeg == false && isWrong == false){
            System.out.println("Rule is wrong not enough information");
        }

    }

    private static int isGenericAndOrReportSetValid(ArrayList<Report> reports, Node RuleNode, ArrayList<Node> arguments, ArrayList<Node> ReqNodes){

        System.out.println("Is AndOr report set valid");
        System.out.println("---------------------------");

        int isValid = 0;

        for (Report rep : reports){
            for (Support sup : rep.getSupports()){

                boolean containsIntroRule = sup.getOriginSet().getHypothesisSet().getPropositions().contains(RuleNode);
                boolean containsNoArguments = Collections.disjoint(arguments, sup.getOriginSet().getHypothesisSet().getPropositions());
                boolean containsReqNodes = sup.getOriginSet().getHypothesisSet().getPropositions().containsAll(ReqNodes);

                ArrayList<Node> commonNodes = new ArrayList<Node>(arguments);
                commonNodes.retainAll(sup.getOriginSet().getHypothesisSet().getPropositions());


                if (!containsIntroRule && containsNoArguments) {
                    System.out.println("Failed, supports do not depend on the antecedents of the rule");
                    return 3;
                }

                if (!containsIntroRule && (commonNodes.size() > ((AndOrNode) RuleNode).getMax() || commonNodes.size() < ((AndOrNode) RuleNode).getMin())){
                    return 4;
                }


                if (rep.getSign() == false && !containsIntroRule && containsReqNodes){
                    return -1;
                }

                if (rep.getSign() == true && !containsIntroRule && containsReqNodes){
                    isValid = 1;
                }

            }
        }


        return isValid;
    }

    private static void GenericThreshRuleHandler(PropositionNode ThreshNode, int ContextID){
        System.out.println("Generic Thresh Handler");
        System.out.println("===========================");

        ArrayList<Node> arguments = new ArrayList<Node>();
        ArrayList<Context> TempContexts = new ArrayList<Context>();
        int contextNum = 0;
        boolean isNeg = false;
        boolean isWrong = false;

        for(Node ant : ThreshNode.getDownCableSet().getDownCable("arg").getNodeSet()){arguments.add(ant);}
        System.out.println("");
        System.out.println("Arguments of rule " + arguments);

        ArrayList<ArrayList<Node>> ReqNodes = Combinations(arguments.size(), ((ThreshNode) ThreshNode).getThresh(), arguments);

        ReqNodes.add(arguments);

        System.out.println(ReqNodes);


    }

    private static ArrayList<Context> GetGenericAndOrTempContext(int argumentsNum, int AndOrMax, ArrayList<Node> arguments, int ContextID, ArrayList<ArrayList<Node>> ReqNodes){

        ArrayList<Context> TempContexts = new ArrayList<Context>();

        for (ArrayList<Node> nodes : ReqNodes){

            TempContexts.add(getTempContext(ContextID, nodes));

        }

        return TempContexts;

    }

    private static void ConcludeIntroduction(boolean wrongRule, int PosCount, int NegCount, int NoOfConsequences){

        if (wrongRule == true) {
            System.out.println("Rule is wrong and can not be introduced");
        } else if (NegCount > 0) {
            System.out.println("Rule is wrong and can not be introduced, but its negation can be introduced");
        } else if (NoOfConsequences == PosCount && NegCount == 0){
            System.out.println("Rule is correct and can be introduced");
        } else {
            System.out.println("Failed to introduce the Rule, not enough information");
        }
    }

    private static Context getTempContext(int ContextID, ArrayList<Node> nodes) {
//        System.out.println("---------------------------");

        Context tempContext = new Context(SNeBR.getContextByID(ContextID));
        SNeBR.setCurrentContext(tempContext);

//        if (!tempContext.getHypothesisSet().getPropositions().contains(RuleNode)){
//            tempContext.addToContext(RuleNode);
//        }

        for (Node node : nodes){
            if (!((Proposition) node.getSemantic()).isAsserted(tempContext)){
                SNeBR.assertProposition((PropositionNode) node);
                tempContext.addToContext((PropositionNode) node);
//                System.out.println(node + " is asserted");
            } else {
//                System.out.println(node + " is already asserted");
            }
        }


//        System.out.println(tempContext.getHypothesisSet().getPropositions());
//        System.out.println(SNeBR.getContextByID(ContextID).getHypothesisSet().getPropositions());
        return tempContext;

    }

    private static ArrayList<Report> getConsequentReports (Node consequent, Node rule, Context context){

        ArrayList<Report> RepSet = new ArrayList<>();

//        ---------------------------------------------------------
//        for And Entailemt test

        if (rule.getClass().getName().contains("AndNode")) {
            RepSet = IntroTest.getIntroReports();
//            RepSet.get(1).getSupports().forEach((sup)-> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));
        }

//        for Or Entailment test

//        System.out.println("--------------");
        if (rule.getClass().getName().contains("OrNode")){
            RepSet = IntroTest.getIntroReports2();
//            RepSet.get(1).getSupports().forEach((sup)-> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));

        }

//        ---------------------------------------------------------

//         for Numerical Entailment test

//        System.out.println("--------------");
        if (rule.getClass().getName().contains("NumericalNode")){
            RepSet = IntroTest.getIntroReports3();
//            RepSet.get(3).getSupports().forEach((sup)-> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));

        }

//        ---------------------------------------------------------

        return RepSet;
    }

    private static ArrayList<Report> getConsequentReports (Node rule, Context context, int gen){

        ArrayList<Report> RepSet = new ArrayList<>();

//        ---------------------------------------------------------
//        for AndOr test

        if (rule.getClass().getName().contains("AndOrNode") && gen < 0) {
            RepSet = IntroTest.getIntroReports4();
//            RepSet.get(1).getSupports().forEach((sup)-> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));
        }

//        for Thresh test
        if (rule.getClass().getName().contains("ThreshNode") && gen < 0) {
            RepSet = IntroTest.getIntroReports5();
//            RepSet.get(1).getSupports().forEach((sup)-> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));
        }

        if (rule.getClass().getName().contains("AndOrNode") && gen > 0) {
            RepSet = IntroTest.getIntroReports6();
//            RepSet.get(4).getSupports().forEach((sup)-> System.out.println(sup.getOriginSet().getHypothesisSet().getPropositions()));
        }

        return RepSet;
    }


    public static ArrayList<ArrayList<Node>> Combinations(int n, int k, ArrayList<Node> nodes) {
        ArrayList<ArrayList<Node>> rslt = new ArrayList<ArrayList<Node>>();
        dfs(new Stack<Node>(), 1, n, k, rslt, nodes);
        return rslt;
    }

    private static void dfs(Stack<Node> path, int index, int n, int k, ArrayList<ArrayList<Node>> rslt, ArrayList<Node> nodes){
        // ending case
        if(k==0){
            ArrayList<Node> list = new ArrayList<Node>(path);
            rslt.add(list);
            return;
        }
        // recursion case
        for(int i = index;i <= n;i++){
            path.push(nodes.get(i-1));
            dfs(path, i+1, n, k-1, rslt, nodes);
            path.pop();
        }
    }

}
