package sneps.match;

import java.util.HashMap;

import sneps.Nodes.Node;
import sneps.Nodes.VariableNode;

public class HashSubstitution implements Substitutions {
    HashMap<VariableNode,Node> sub;
    
     public HashSubstitution() {
		sub=new HashMap<VariableNode,Node>();
	}
	
	public boolean isNew() {
		return sub.isEmpty();
	}

	
	public void putIn(Binding mb) {
		
		sub.put(mb.getVariable(), mb.getNode());

	}

	
	public boolean isCompatible(Binding mb) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void update(Binding mb, Node mn) {
		sub.put(mb.getVariable(), mn);

	}

	
	public boolean isBound(VariableNode mv) {
		return sub.containsKey(mv);
	}

	
	public boolean isValue(Node mn) {
		return sub.containsValue(mn);
	}

	
	public VariableNode srcNode(Node mn) {
		VariableNode [] variables=(VariableNode []) sub.keySet().toArray();
		for(VariableNode variable:variables)
			if(sub.get(variable).equals(mn))
				return variable;
		return null;
	}

	
	public Binding getBindingByVariable(VariableNode mv) {
		return sub.containsKey(mv)?new Binding(mv, sub.get(mv)):null;
		
	}

	
	public Binding getBindingByNode(Node mn) {
		VariableNode key=srcNode(mn);
		return key==null?null:new Binding(key,mn);
		}

	
	public boolean isMember(Binding mb) {
		Node node=sub.get(mb.getVariable());
		return node!=null&&node==mb.getNode();
	}

	@Override
	public boolean isSubSet(Substitutions s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEqual(Substitutions s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Substitutions union(Substitutions s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unionIn(Substitutions s) {
		// TODO Auto-generated method stub

	}

	
	public Substitutions restrict(VariableNode[] variables) {
		HashSubstitution hs=new HashSubstitution();
		for(VariableNode variable:variables)
		hs.putIn(new Binding(variable,sub.get(variable)));	
		
		return hs;
	}

	
	public Node term(VariableNode mv) {
		return sub.get(mv);
	}

	
	public int cardinality() {
		return sub.size();
	}

	@Override
	public Binding choose() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Substitutions others() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Node value(VariableNode v) {
		Node n=sub.get(v);
		return n==null?v:n;
	}

	@Override
	public Substitutions insert(Binding m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCompatible(Substitutions s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Binding getBinding(int x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Substitutions[] split() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		sub=new HashMap<VariableNode,Node>();

	}

	@Override
	public void insert(Substitutions s) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean sub(String x, String y) {
        for(int i=0;i<y.length();i++)
        {
                if(y.charAt(i)!=x.charAt(i))
                        return false;
        }
        return true;
}

	public String toString(){
		//TODO: Auto-generated method stub
		return null;
	}
	
}