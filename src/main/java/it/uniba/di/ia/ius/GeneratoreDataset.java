package it.uniba.di.ia.ius;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeneratoreDataset {

    private static final int DEFAULT_NUM_FILES = 1;

    public static void main(String[] args) {

        int numFiles;
        if (args.length > 0) {
            numFiles = Integer.parseInt(args[0]);
        } else {
            numFiles = DEFAULT_NUM_FILES;
        }

        File file = new File("./prolog/dataset.pl");
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            for (int i = 0; i < numFiles; i++) {
                fw.append(":- kb:assertDoc(\"");
                double randomNum = Math.random();
                if (randomNum < (1.0 / 3.0))
                    fw.append(modulo1(new InfoModulo()));
                else if (randomNum < 2.0 / 3.0)
                    fw.append(modulo2(new InfoModulo()));
                else
                    fw.append(modulo3(new InfoModulo()));
                fw.append("\").\n\n\n");
                System.out.println("Modulo " + i + " scritto su file.");
            }
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String modulo1(InfoModulo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("TRIBUNALE CIVILE DI ");
        sb.append(info.getComuneTribunale());
        sb.append("\\nAll'Ill.mo Giudice Delegato al fallimento ");
        sb.append(info.getCognomeGiudice() + " " + info.getNomeGiudice());
        sb.append("\\nn. pratica ");
        sb.append(info.getNumeroPratica());
        sb.append("\\nISTANZA DI INSINUAZIONE ALLO STATO PASSIVO\\n");
        sb.append("\\nIl sottoscritto, ");
        sb.append(info.getCognomeSottoscritto() + " " + info.getNomeSottoscritto());
        sb.append(", nato a ");
        sb.append(info.getComuneNascita());
        sb.append(" il ");
        sb.append(info.getDataNascita());
        sb.append(", elettivamente domiciliato agli effetti del presente atto in ");
        sb.append(info.getIndirizzo());
        sb.append("\\nRecapito tel. ");
        sb.append(info.getTelefono());
        sb.append(", Codice Fiscale: ");
        sb.append(info.getCodiceFiscale());
        sb.append(", indirizzo e-mail ");
        sb.append(info.getEmail());
        sb.append("\\nDICHIARA\\ndi essere creditore nei confronti della Ditta di cui sopra, della somma dovutagli per ");
        sb.append(info.getMotivazioneRichiesta());
        sb.append(" per il periodo dal ");
        sb.append(info.getDataInizio());
        sb.append(" al ");
        sb.append(info.getDataFine());
        sb.append(".\\nTotale avere: ");
        sb.append(info.getValuta());
        sb.append(". Come da giustificativi allegati.\\nPERTANTO CHIEDE\\nl'ammissione allo stato passivo della ");
        sb.append("procedura in epigrafe dell'importo di ");
        sb.append(info.getValuta());
        sb.append(" ");
        sb.append(info.getTipoRichiesta());
        sb.append(" oltre rivalutazione monetaria ed interessi di legge fino alla data di chiusura dello stato ");
        sb.append("passivo e soli interessi legali fino alla liquidazione delle attività mobiliari da quantificarsi ");
        sb.append("in sede di liquidazione,\\n");
        sb.append(info.getComuneTribunale());
        sb.append(", li ");
        sb.append(info.getDataOggi());
        sb.append("\\n");
        sb.append(info.getCognomeSottoscritto() + " " + info.getNomeSottoscritto());
        sb.append("\\nSi allegano ");
        sb.append(info.getNumeroAllegati());
        sb.append(" documenti:");
        for (int idx = 1; idx <= info.getNumeroAllegati(); idx++) {
            sb.append("\\nfattura n. " + idx);
        }
        return sb.toString();
    }

    public static String modulo2(InfoModulo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("DOMANDA DI AMMISSIONE ALLO STATO PASSIVO");
        sb.append("\\nTRIBUNALE CIVILE DI ");
        sb.append(info.getComuneTribunale());
        sb.append("\\nSEZIONE FALLIMENTARE\\nProcedura n. ");
        sb.append(info.getNumeroPratica());
        sb.append("\\nGIUDICE DELEGATO -  ");
        sb.append(info.getCognomeGiudice() + " " + info.getNomeGiudice());
        sb.append("\\nCURATORE - ");
        sb.append(info.getCognomeCuratore() + " " + info.getNomeCuratore());
        sb.append("\\nDOMANDA DI AMMISSIONE AL PASSIVO");
        sb.append("\\nIll.mo signor Giudice Delegato alla procedura sopra indicata,\n");
        sb.append("il sottoscritto ");
        sb.append(info.getCognomeSottoscritto() + " " + info.getNomeSottoscritto());
        sb.append(" nato a ");
        sb.append(info.getComuneNascita());
        sb.append(" il " + info.getDataNascita() + " con sede in " + info.getComuneNascita());
        sb.append(" C.F. " + info.getCodiceFiscale() + " domiciliato in " + info.getComuneResidenza() + " in " + info.getIndirizzo());
        sb.append(" il quale dichiare di voler ricevere comunicazioni e notifiche a mezzo fax al seguente n. " + info.getTelefono());
        sb.append(" oppure per email al seguente indirizzo " + info.getEmail());
        sb.append("\\nCHIEDE\\n");
        sb.append("di essere ammesso allo stato passivo della procedure in epigrafe indicata : \\n");
        sb.append("in via chirografaria per " + info.getValuta() + " Precisando che il proprio credito deriva da: \\n " + info.getMotivazioneRichiesta());
        sb.append("\\nSi allegano ");
        sb.append(info.getNumeroAllegati());
        sb.append(" documenti:");
        for (int idx = 1; idx <= info.getNumeroAllegati(); idx++) {
            sb.append("\\nfattura n. " + idx);
        }
        sb.append("\\nin via privilegiata per " + info.getNValuta() + " Precisando che il proprio credito deriva da: \\n " + info.getNMotivazioneRichiesta());
        sb.append("\\nSi allegano ");
        sb.append(info.getNumeroAllegati());
        sb.append(" documenti:");
        for (int idx = 1; idx <= info.getNumeroAllegati(); idx++) {
            sb.append("\\nfattura n. " + idx);
        }
        sb.append("\\n" + info.getComuneTribunale());
        sb.append(", li ");
        sb.append(info.getDataOggi());
        sb.append("\\n");
        sb.append(info.getCognomeSottoscritto() + " " + info.getNomeSottoscritto());
        return sb.toString();
    }

    public static String modulo3(InfoModulo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("TRIBUNALE CIVILE E PENALE DI ");
        sb.append(info.getComuneTribunale());
        sb.append("\\nSEZIONE FALLIMENTARE");
        sb.append("\\nFallimento: " + info.getAziendaFallimento());
        sb.append("\\nSentenza n. ");
        sb.append(info.getNumeroPratica());
        sb.append("\\nGiudice Delegato: " + info.getCognomeGiudice() + " " + info.getNomeGiudice());
        sb.append("\\nCuratore: " + info.getCognomeCuratore() + " " + info.getNomeCuratore());
        sb.append("\\nVerifica dei Crediti: " + info.getDataVerifica());
        sb.append("\\nDomanda di ammissione al passivo");
        sb.append("\\nA norma dell'art 93 LF");
        sb.append("\\nIl sottoscritto " + info.getCognomeSottoscritto() + " " + info.getNomeSottoscritto());
        sb.append(" con studio in " + info.getIndirizzo() + " ");
        sb.append(" tel. " + info.getTelefono());
        sb.append(" cod. fis. " + info.getCodiceFiscale());
        sb.append(" email " + info.getEmail());
        sb.append("\\nPREMESSO");
        sb.append("\\ndi risultare creditore della società fallita, dichiarata in epigrafe, per prestazioni professionali per i seguenti importi: \\n");
        sb.append(info.getValuta() + " per onorari, oltre Iva e CP 4% \\n come da parcella allegata.");
        sb.append("\\nTanto premesso, il sottoscritto " + info.getCognomeSottoscritto() + " " + info.getNomeSottoscritto() + " , porge rispettosa");
        sb.append("\\nISTANZA");
        sb.append("\\naffinché la S.V. Ill.ma voglia ammetterlo al passivo del fallimento in epigrafe per i seguenti importi:\\n");
        sb.append(info.getValuta() + " oltre CP 4% al privilegio ex art.2753 bis n.2 c.c nonché oltre iva al chirografo per " + info.getNValuta());
        sb.append("\\nSi allegano ");
        sb.append(info.getNumeroAllegati());
        sb.append(" documenti:");
        for (int idx = 1; idx <= info.getNumeroAllegati(); idx++) {
            sb.append("\\nfattura n. " + idx);
        }
        sb.append("\\n" + info.getComuneTribunale());
        sb.append(", li ");
        sb.append(info.getDataOggi());
        sb.append("\\n");
        sb.append(info.getCognomeSottoscritto() + " " + info.getNomeSottoscritto());
        return sb.toString();
    }
}
