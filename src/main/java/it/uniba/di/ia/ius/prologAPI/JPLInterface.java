package it.uniba.di.ia.ius.prologAPI;

import jpl.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JPLInterface extends PrologInterface {

    public JPLInterface(int type) {
        super(type);
        self = this;
        switch (type) {
            case SWI:
                jpl.JPL.setNativeLibraryDir(SWI_LIB_PATH);
                break;
            case YAP:
                jpl.JPL.setNativeLibraryDir(YAP_LIB_PATH);
                break;
            default:
                break;
        }
        jpl.JPL.init();
    }

    @Override
    public void close() {

    }

    @Override
    public void consult(File file) {
        Term t = new Compound("consult", new Term[]{new Atom(file.getAbsolutePath())});
        Query query = new Query(t);
        query.hasSolution();
        query.close();
    }

    @Override
    public void asserta(String pred, List<String> args) {
        metaCommand("asserta", pred, args);
    }

    @Override
    public void assertz(String pred, List<String> args) {
        metaCommand("assertz", pred, args);
    }

    @Override
    public void retract(String pred, List<String> args) {
        metaCommand("retract", pred, args);
    }

    @Override
    public void retractAll(String pred, List<String> args) {
        metaCommand("retractall", pred, args);
    }

    private void metaCommand(String command, String pred, List<String> args) {
        Term toSend;
        if ((args == null) || (args.size() == 0))
            toSend = new Atom(pred);
        else {
            Term[] termArgs = new Term[args.size()];
            for (int i = 0; i < args.size(); i++) {
                termArgs[i] = Util.textToTerm(args.get(i));
            }
            toSend = new Compound(pred, termArgs);
        }
        Term t = new Compound(command, new Term[]{toSend});
        Query query = new Query(t);
        System.err.println("QUERY = " + query.toString());
        query.hasSolution();
        query.close();
    }

    @Override
    public boolean statisfied(String pred, List<String> args) {
        Term term;
        if ((args == null) || (args.size() == 0))
            term = new Atom(pred);
        else {
            Term[] termArgs = new Term[args.size()];
            for (int i = 0; i < args.size(); i++) {
                termArgs[i] = Util.textToTerm(args.get(i));
            }
            term = new Compound(pred, termArgs);
        }
        Query query = new Query(term);
        return query.hasSolution();
    }

    @Override
    public Map<String, String> oneSolution(String pred, List<String> args) throws NoVariableException {
        Map<String, String> map = new HashMap<>();
        List<String> vars = new ArrayList<>(args.size());
        Term term;
        if ((args == null) || (args.size() == 0))
//            term = new Atom(pred);
            throw new NoVariableException();
        else {
            Term[] termArgs = new Term[args.size()];
            for (int i = 0; i < args.size(); i++) {
                String arg = args.get(i);
                termArgs[i] = Util.textToTerm(arg);
                if (prologNamedVariable(arg)) {
                    vars.add(arg);
                }
            }
            if (vars.isEmpty())
                throw new NoVariableException();
            term = new Compound(pred, termArgs);
        }
        Query query = new Query(term);
        java.util.Hashtable<String, Term> ht = query.oneSolution();
        for (String var : vars) {
            map.put(var, ht.get(var).toString().replaceAll("'", "").replaceAll("\\\\n", "\n"));
        }
        return map;

    }


    @Override
    public List<Map<String, String>> allSolutions(String predOrig, List<String> args) {

        String pred = "all" + predOrig;
        List<Map<String, String>> listMap = new ArrayList<>(10);
        List<String> vars = new ArrayList<>(args.size());
        Term term;
        Term[] termArgs = new Term[1];
        termArgs[0] = Util.textToTerm(args.get(0));

        for (int i = 0; i < args.size(); i++)
            if (prologNamedVariable(args.get(i))) {
                vars.add(args.get(i));
            }

        term = new Compound(pred, termArgs);
        Query query = new Query(term);
        java.util.Hashtable<String, Term>[] hts = query.allSolutions();
        Term result = hts[0].get("R0");

        String[] resultSplit = readall(result).replace("\',\'", "").replaceAll("\\(", "").replaceAll("\\)", "").split("\n");
        for (String s : resultSplit) {
            if (s.length() > 0) {
                Map<String, String> map = new HashMap<>();
                String[] varsExtract = s.split(", ");
                for (int i = 0; i < varsExtract.length; i++) {
                    map.put(vars.get(i), varsExtract[i].replaceAll("\'", ""));
                }
                listMap.add(map);
            }
        }


        return listMap;
    }

    private String readall(Term term) {
        if (term.arity() == 2) {
            String val = term.arg(1).toString() + " \n";
            return val + readall(term.arg(2));
        }
        return "";
    }
}
