package snip.IntroductionRulesProcess;


import sneps.Nodes.Node;
import sneps.match.Substitutions;
import snip.*;

import java.util.ArrayList;

public class IntroductionChannel extends Channel {

    private ArrayList<Report> Introreports;

    public IntroductionChannel(Substitutions switchSubstitution, Substitutions filterSubstitutions, int contextID,
                                   Node consequence, Node rule, boolean v) {
        super(switchSubstitution, filterSubstitutions, contextID, consequence, rule, v);
    }


}
