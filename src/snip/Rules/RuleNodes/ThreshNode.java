package snip.Rules.RuleNodes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sneps.Nodes.NodeSet;
import sneps.SemanticClasses.Proposition;
import sneps.SyntaticClasses.Molecular;
import snip.Channel;
import snip.Report;
import snip.Rules.DataStructures.FlagNode;
import snip.Rules.DataStructures.RuleUseInfo;
import SNeBR.Context;

public class ThreshNode extends RuleNode {

	private int min, max, arg;

	public ThreshNode(Molecular syn, Proposition sym) {
		super(syn, sym);
		NodeSet minNode = this.getDownNodeSet("thresh");
		min = Integer.parseInt(minNode.getNode(0).getIdentifier());
		NodeSet maxNode = this.getDownNodeSet("threshmax");
		max = Integer.parseInt(maxNode.getNode(0).getIdentifier());
		NodeSet antNodes = this.getDownNodeSet("arg");
		arg = antNodes.size();
		this.processNodes(antNodes);
	}

	protected void sendRui(RuleUseInfo ruiRes, Context context) {
		// TODO Mussab Compute Support
		boolean sign = false;
		if (ruiRes.getPosCount() == min
				&& ruiRes.getNegCount() == arg - max - 1)
			sign = true;
		else if (ruiRes.getPosCount() != min - 1
				|| ruiRes.getNegCount() != arg - max)
			return;

		Set<Integer> consequents = new HashSet<Integer>();
		for (FlagNode fn : ruiRes.getFlagNodeSet()) {
			if (antNodesWithVarsIDs.contains(fn.getNode().getId()))
				continue;
			if (antNodesWithoutVarsIDs.contains(fn.getNode().getId()))
				continue;
			consequents.add(fn.getNode().getId());
		}

		Report reply = new Report(ruiRes.getSub(), null, sign, context.getId());
		Channel ch = null;
		for (Iterator<Channel> iter = outgoingChannels.getIterator(); iter
				.hasNext();) {
			ch = iter.next();
			if (!consequents.contains(ch.getDestination().getId()))
				continue;
			ch.addReport(reply);
		}

	}

	public int getThresh() {
		return min;
	}

	public int getThreshMax() {
		return max;
	}

	public int getArg() {
		return arg;
	}

}