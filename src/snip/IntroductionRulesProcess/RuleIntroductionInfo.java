package snip.IntroductionRulesProcess;


import sneps.Nodes.Node;
import snip.Report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RuleIntroductionInfo {

    private Node Requester;
    private ArrayList<Node> Antecedents;
    private ArrayList<Node> Consequents;
    private ArrayList<Report> ReportSet;
    private int PosCount;
    private int NegCount;
    private boolean Complete;

    public RuleIntroductionInfo(Node requester, ArrayList<Node> antecedents, ArrayList<Node> consequents){
        this.Requester = requester;
        this.Antecedents = antecedents;
        this.Consequents = consequents;
        this.ReportSet = new ArrayList<Report>();
        this.PosCount = 0;
        this.NegCount = 0;
        this.Complete = false;
    }

    public void addReport(Report report){
        this.ReportSet.add(report);
    }

    public void setReportSet(ArrayList<Report> reportSet) {
        ReportSet = reportSet;
    }

    public void setRequester(Node requester) {
        Requester = requester;
    }

    public void setAntecedents(ArrayList<Node> antecedents) {
        Antecedents = antecedents;
    }

    public void setConsequents(ArrayList<Node> consequents) {
        Consequents = consequents;
    }

    public void setPosCount(int posCount) {
        PosCount = posCount;
    }

    public void setNegCount(int negCount) {
        NegCount = negCount;
    }

    public void setComplete(boolean complete) {
        Complete = complete;
    }


    public Node getRequester() {
        return Requester;
    }

    public ArrayList<Node> getAntecedents() {
        return Antecedents;
    }

    public ArrayList<Node> getConsequents() {
        return Consequents;
    }

    public ArrayList<Report> getReportSet() {
        return ReportSet;
    }

    public int getPosCount() {
        return PosCount;
    }

    public int getNegCount() {
        return NegCount;
    }

    public boolean isComplete() {
        return Complete;
    }

}
