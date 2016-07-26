package snip.IntroductionRulesProcess;
/*

This class contains the methods responsible for applying the logic behind introduction rules, in order to decide mainly if rule can or can not be introduced.

The main structure of this class is firstly, a main method (BeginIntroduction) responsible initiating the introduction process. Secondly, for each logical connective in SNePS a handler method (e.g. AndEntRuleHandler) is responsible for applying its logic.

The process starts with acquiring the channel with the rule that wises to be introduced, then checks whether the rule is valid to be introduced or not. If the check fails we stop and rule introduction does not introduce the rule, on the other hand, if it passes the check, each connective (e.g. And-Entailment, Or-Entailment, ...) is then handled by its corresponding handler method. Lastly, after the handler method is done, the result is whether the rule is valid to be introduced or not.

Note: Due to some problems occurring when retrieving all the reports from querying the system, the method responsible for retrieving the reports is custom made for the test class "IntroTest", in order to simulate and test the introduction process on its own.
 */

import SNeBR.SNeBR;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.xpath.internal.SourceTree;
import sneps.CaseFrame;
import sneps.Network;
import sneps.Nodes.*;
import SNeBR.Context;
import SNeBR.Support;
import sneps.Relation;
import sneps.SemanticClasses.Individual;
import sneps.SemanticClasses.Proposition;
import snip.Channel;
import snip.Report;
import snip.Rules.RuleNodes.AndOrNode;
import snip.Rules.RuleNodes.NumericalNode;
import snip.Rules.RuleNodes.ThreshNode;

import java.util.*;

public class IntroductionRule {

//    This method is responsible for taking the channel with the rule that wants to be introduced, then applying a check to see if its possible or not, if it fails then whole process stops, if not, then it passes the rule to its corresponding handler method.

    public static void BeginIntroduction(Channel channel){

        Node Node = channel.getReporter();
        int ContextID = channel.getContextID();


        if (CanBeIntroduced((PropositionNode) Node, channel)) {

            if (Node.getClass().getName().contains("AndOrNode")) {
                System.out.println("This is AndOr node : " + Node + " Starting introduction");
//                AndOrRuleHandler((PropositionNode) Node, ContextID);
                GenericAndOrRuleHandler((PropositionNode) Node, ContextID);
            } else if (Node.getClass().getName().contains("ThreshNode")) {
                System.out.println("This is Thresh node : " + Node + " Starting introduction");
//                ThreshRuleHandler((PropositionNode) Node, ContextID);
                GenericThreshRuleHandler((PropositionNode) Node, ContextID);
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

//    This method is responsible for checking if the rule has all the properties to be introduced or not.

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

//    This method is responsible for handling the logic to decide whether the And-Entailment should be introduced or not.

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

//    This method is responsible for processing all the reports of the And-Entailment, and returns whether the reports can support consequence or not.

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

    //    This method is responsible for handling the logic to decide whether the Or-Entailment should be introduced or not.

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

    //    This method is responsible for processing all the reports of the Or-Entailment, and returns whether the reports can support consequence or not.


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

    //    This method is responsible for handling the logic to decide whether the Numerical-Entailment should be introduced or not.

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

    //    This method is responsible for processing all the reports of the Numerical-Entailment, and returns whether the reports can support consequence or not.


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

    //    This method is responsible for handling the logic to decide whether the AndOr should be introduced or not.

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

    //    This method is responsible for processing all the reports of the AndOr, and returns whether the reports can support consequence or not.

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

    //    This method is responsible for handling the logic to decide whether the Thresh should be introduced or not.

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

    //    This method is responsible for processing all the reports of the Thresh, and returns whether the reports can support consequence or not.

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

    //    This method is responsible for handling the logic to decide whether the general And-Entailment should be introduced or not. This method is similar to And-Entailment however, it applies a more generic way.

    private static int GenericAndOrRuleHandler(PropositionNode AndOrNode, int ContextID){

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

        ArrayList<ArrayList<Node>> ReqPerm2 = Combinations(arguments.size(), arguments.size() - ((AndOrNode) AndOrNode).getMin() , arguments);

        ArrayList<ArrayList<Node>> ReqNodes2 = new ArrayList<ArrayList<Node>>();

        for (ArrayList<Node> nodes : ReqPerm2){
            ArrayList<Node> TempArguments = new ArrayList<Node>(arguments);

            TempArguments.removeAll(nodes);
            ReqNodes2.add(TempArguments);
        }


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

        if (isWrong){
            System.out.println("Rule is wrong");
            return 0;
        } else if (isNeg == true && isWrong == false){
            System.out.println("Rule can not be introduced, but its negation can be introduced");
            return -1;
        } else if (contextNum == TempContexts.size() && isNeg == false && isWrong == false){
            System.out.println("Rule is correct and can be introduced");
            return 1;
        } else {
            System.out.println("Rule is wrong not enough information");
            return  2;
        }

    }

    //    This method is responsible for processing all the reports of the generic And-Entailment, and returns whether the reports can support consequence or not.


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

    //    This method is responsible for handling the logic to decide whether the general Thresh should be introduced or not. This method is similar to Thresh however, it applies a more generic way.

    private static void GenericThreshRuleHandler(PropositionNode ThreshNode, int ContextID) {
        System.out.println("Generic Thresh Handler");
        System.out.println("===========================");

        ArrayList<Node> arguments = new ArrayList<Node>();

        for (Node ant : ThreshNode.getDownCableSet().getDownCable("arg").getNodeSet()) {
            arguments.add(ant);
        }

        Individual individual = new Individual();
        Node ThreshMin = null;
        try {
            ThreshMin = Network.buildBaseNode(Integer.toString(((ThreshNode) ThreshNode).getThresh()), individual);
        } catch (Exception exp) {
        }


        individual = new Individual();
        Node ThreshMax = null;
        try {
            ThreshMax = Network.buildBaseNode(Integer.toString(((ThreshNode) ThreshNode).getThreshMax()), individual);
        } catch (Exception exp) {
        }

        individual = new Individual();
        Node zero = null;
        try {
            zero = Network.buildBaseNode("0", individual);
        } catch (Exception exp) {
        }

        individual = new Individual();
        Node maxArguments = null;
        try {
            maxArguments = Network.buildBaseNode(Integer.toString(arguments.size()), individual);
        } catch (Exception exp) {
        }

        Object[][] a1 = new Object[(arguments.size()) + 2][2];

        for (int i = 0; i < arguments.size(); i++) {
            a1[i][0] = Relation.arg;
            a1[i][1] = arguments.get(i);
        }

        a1[arguments.size()][0] = Relation.min;
        a1[arguments.size()][1] = zero;
        a1[(arguments.size()) + 1][0] = Relation.max;
        a1[(arguments.size()) + 1][1] = ThreshMin;

        AndOrNode AndOrRule1 = null;
        AndOrNode AndOrRule2 = null;

        try {
            AndOrRule1 = (AndOrNode) Network.buildMolecularNode(a1, CaseFrame.andOrRule);
        } catch (Exception exp) {
        }

        System.out.println(AndOrRule1);

        a1[arguments.size()][0] = Relation.min;
        a1[arguments.size()][1] = ThreshMax;
        a1[(arguments.size()) + 1][0] = Relation.max;
        a1[(arguments.size()) + 1][1] = maxArguments;

        try {
            AndOrRule2 = (AndOrNode) Network.buildMolecularNode(a1, CaseFrame.andOrRule);
        } catch (Exception exp) {
        }

        System.out.println(AndOrRule2);

        int ResultOfAndOrRule1 = GenericAndOrRuleHandler(AndOrRule1, ContextID);
        int ResultOfAndOrRule2 = GenericAndOrRuleHandler(AndOrRule2, ContextID);

        System.out.println("################################");
        System.out.println("################################");

        if (ResultOfAndOrRule1 == 1 && ResultOfAndOrRule2 == 1){

            System.out.println("Rule is correct and can be introduced");
        }

        if (ResultOfAndOrRule1 == -1 && ResultOfAndOrRule2 == -1) {
            System.out.println("Rule can not be introduced, but its negation can be introduced");
        }

        if (ResultOfAndOrRule1 == 2 && ResultOfAndOrRule2 == 2) {
            System.out.println("Rule is wrong not enough information");
        }

        System.out.println("Rule is wrong and can not be introduced");

    }

//    This method is responsible for creating a new context for each group of required nodes.

    private static ArrayList<Context> GetGenericAndOrTempContext(int argumentsNum, int AndOrMax, ArrayList<Node> arguments, int ContextID, ArrayList<ArrayList<Node>> ReqNodes){

        ArrayList<Context> TempContexts = new ArrayList<Context>();

        for (ArrayList<Node> nodes : ReqNodes){

            TempContexts.add(getTempContext(ContextID, nodes));

        }

        return TempContexts;

    }

//    This method is responsible for just deciding which of four outcomes of introduction process should be concluded.

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

//    This method is responsible for creating a new context with nodes given.

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

//    This method is responsible for getting the reports from test class (for And-Entailment, Or-Entailment, ...) just to simulate the process and must be changed when some problems are fixed.

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

    //    This method is responsible for getting the reports from test class (for AndOr & thresh) just to simulate the process and must be changed when some problems are fixed.

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

//    This method is responsible for getting all the possible combinations of nodes given.

    public static ArrayList<ArrayList<Node>> Combinations(int n, int k, ArrayList<Node> nodes) {
        ArrayList<ArrayList<Node>> rslt = new ArrayList<ArrayList<Node>>();
        dfs(new Stack<Node>(), 1, n, k, rslt, nodes);
        return rslt;
    }

//    This method is responsible for recursively making all the possible combinations for the combinations method.

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




