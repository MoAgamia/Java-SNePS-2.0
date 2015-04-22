package snip.Rules;

import sneps.SemanticClasses.Proposition;
import sneps.SyntaticClasses.Molecular;
import snip.Report;

public class OR extends Rule {

	public OR(Molecular syn, Proposition sym) {
		super(syn, sym);
	}

	@Override
	public void applyRuleHandler(Report report) {
		if (report.isNegative())
			return;

		Report reply = new Report(report.getSubstituions(),
				report.getSupport(), true, this, null, report.getContext());
		// TODO
		throw new UnsupportedOperationException();
		// ChannelsSet ctemp = this.getOutGoing().getConChannelsSet(
		// r.getContext());
		// this.sendReport(reply, ctemp);
	}

	@Override
	public void resetRule() {
	}
}