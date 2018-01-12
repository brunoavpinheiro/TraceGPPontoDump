import org.apache.commons.cli.*;

public class Main {

    public static void main(String[] args) {
        Options options = new Options();

        Option inputAddress = new Option("e", "endereco", true, "Endereço do TraceGP");
        inputAddress.setRequired(true);
        options.addOption(inputAddress);

        Option inputUser = new Option("u", "usuario", true, "Usuário do TraceGP");
        inputUser.setRequired(true);
        options.addOption(inputUser);

        Option inputSenha = new Option("s", "senha", true, "Senha do TraceGP");
        inputSenha.setRequired(true);
        options.addOption(inputSenha);

        Option dataInicial = new Option("di", "dataInicial", true, "Data inicial");
        dataInicial.setRequired(true);
        options.addOption(dataInicial);

        Option dataFinal = new Option("df", "dataFinal", true, "Data final");
        dataFinal.setRequired(true);
        options.addOption(dataFinal);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());

            formatter.printHelp("TraceGPPontoDump", options);

            System.exit(1);
            return;
        }

        PontoDump.Dump(cmd.getOptionValue("endereco"), cmd.getOptionValue("usuario"), cmd.getOptionValue("senha"), cmd.getOptionValue("dataInicial"), cmd.getOptionValue("dataFinal"));
    }
}
