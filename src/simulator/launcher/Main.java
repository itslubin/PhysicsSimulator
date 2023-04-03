package simulator.launcher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.Factory;
import simulator.factories.MovingBodyBuilder;
import simulator.factories.MovingTowardsFixedPointBuilder;
import simulator.factories.NewtonUniversalGravitationBuilder;
import simulator.factories.NoForceBuilder;
import simulator.factories.StationaryBodyBuilder;
import simulator.model.Body;
import simulator.model.ForceLaws;
import simulator.model.PhysicsSimulator;
import simulator.view.MainWindow;


public class Main {
	
	enum Mode {
		BATCH,
		GUI
	}
	
	// default values for some parameters
	//
	private final static Integer _stepsDefaultValue = 150;
	private final static Double _dtimeDefaultValue = 2500.0;
	private final static String _forceLawsDefaultValue = "nlug";

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Integer _steps = null;
	private static Double _dtime = null;
	private static String _inFile = null;
	private static String _outFile = null;
	private static JSONObject _forceLawsInfo = null;
	private static Mode _mode = null;

	// factories
	private static Factory<Body> _bodyFactory;
	private static Factory<ForceLaws> _forceLawsFactory;
	
	

	private static void initFactories() {
		ArrayList<Builder<Body>> bodyBuilders = new ArrayList<>();
		bodyBuilders.add(new MovingBodyBuilder());
		bodyBuilders.add(new StationaryBodyBuilder());
		_bodyFactory = new BuilderBasedFactory<Body>(bodyBuilders);
		
		ArrayList<Builder<ForceLaws>> ForceLawsBuilders = new ArrayList<>();
		ForceLawsBuilders.add(new MovingTowardsFixedPointBuilder());
		ForceLawsBuilders.add(new NoForceBuilder());
		ForceLawsBuilders.add(new NewtonUniversalGravitationBuilder());
		_forceLawsFactory = new BuilderBasedFactory<ForceLaws>(ForceLawsBuilders);
		
	}

	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseHelpOption(line, cmdLineOptions);
			parseInFileOption(line);
			parseDeltaTimeOption(line);
			parseForceLawsOption(line);
			parseOutFileOption(line);
			parseStepsOption(line);
			parseModeOption(line);
			
			

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Bodies JSON input file.").build());

		// delta-time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _dtimeDefaultValue + ".")
				.build());

		// force laws
		cmdLineOptions.addOption(Option.builder("fl").longOpt("force-laws").hasArg()
				.desc("Force laws to be used in the simulator. Possible values: "
						+ factoryPossibleValues(_forceLawsFactory) + ". Default value: '" + _forceLawsDefaultValue
						+ "'.")
				.build());
		
		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written. Default value: the standard output.").build());
		
		// steps
		cmdLineOptions.addOption(Option.builder("s").hasArg().longOpt("steps")
				.desc("An integer representaing the number of simulation steps. Default value: "
						+ _stepsDefaultValue + ".")
				.build());
		
		// mode
		cmdLineOptions.addOption(Option.builder("m").hasArg().longOpt("mode")
				.desc("Execution Mode. Possible values: 'batch' (Batch mode), 'gui' (Graphical User Interface mode). Default value: 'gui'.")
				.build());
		

		return cmdLineOptions;
	}

	public static String factoryPossibleValues(Factory<?> factory) {
		String s = "";
		if (factory != null) {

			for (JSONObject fe : factory.getInfo()) {
				if (s.length() > 0) {
					s = s + ", ";
				}
				s = s + "'" + fe.getString("type") + "' (" + fe.getString("desc") + ")";
			}

			s = s + ". You can provide the 'data' json attaching :{...} to the tag, but without spaces.";
		} else {
			s = "No values found";
		}
		return s;
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
		if (_inFile == null && _mode == Mode.BATCH) {
			throw new ParseException("In batch mode an input file of bodies is required");
		}
	}

	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _dtimeDefaultValue.toString());
		try {
			_dtime = Double.parseDouble(dt);
			assert (_dtime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + dt);
		}
	}
	
	private static void parseOutFileOption(CommandLine line) throws ParseException {
		_outFile = line.getOptionValue("o");
	}
	
	private static void parseStepsOption(CommandLine line) throws ParseException {
		String s = line.getOptionValue("s", _stepsDefaultValue.toString());
		try {
			_steps = Integer.parseInt(s);
			assert (_steps > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid steps value: " + s);
		}
	}
	
	private static void parseModeOption(CommandLine line) throws ParseException {
		String s = line.getOptionValue("m");
		switch(s.toLowerCase()) {
		case "batch" :
			_mode = Mode.BATCH;
			break;
		case "gui":
			_mode = Mode.GUI;
			break;
		}
	}

	private static JSONObject parseWRTFactory(String v, Factory<?> factory) {

		// the value of v is either a tag for the type, or a tag:data where data is a
		// JSON structure corresponding to the data of that type. We split this
		// information
		// into variables 'type' and 'data'
		//
		int i = v.indexOf(":");
		String type = null;
		String data = null;
		if (i != -1) {
			type = v.substring(0, i);
			data = v.substring(i + 1);
		} else {
			type = v;
			data = "{}";
		}

		// look if the type is supported by the factory
		boolean found = false;
		if (factory != null) {
			for (JSONObject fe : factory.getInfo()) {
				if (type.equals(fe.getString("type"))) {
					found = true;
					break;
				}
			}
		}

		// build a corresponding JSON for that data, if found
		JSONObject jo = null;
		if (found) {
			jo = new JSONObject();
			jo.put("type", type);
			jo.put("data", new JSONObject(data));
		}
		return jo;

	}

	private static void parseForceLawsOption(CommandLine line) throws ParseException {
		String fl = line.getOptionValue("fl", _forceLawsDefaultValue);
		_forceLawsInfo = parseWRTFactory(fl, _forceLawsFactory);
		if (_forceLawsInfo == null) {
			throw new ParseException("Invalid force laws: " + fl);
		}
	}
	

	private static void startBatchMode() throws Exception {
		PhysicsSimulator ps = new PhysicsSimulator( _forceLawsFactory.createInstance(_forceLawsInfo), _dtime);
		Controller c = new Controller(ps, _bodyFactory, _forceLawsFactory);
		InputStream in = new FileInputStream(_inFile);
		c.loadData(in);
		OutputStream out = System.out;
		if ( _outFile != null)
			out = new FileOutputStream(_outFile);
		c.run(_steps, out);
		if (_outFile != null)
			out.close();
	}
	
	private static void startGUIMode() throws Exception {
		PhysicsSimulator ps = new PhysicsSimulator( _forceLawsFactory.createInstance(_forceLawsInfo), _dtime);
		Controller c = new Controller(ps, _bodyFactory, _forceLawsFactory);
		
		if (_inFile != null) {
			InputStream in = new FileInputStream(_inFile);
			c.loadData(in);
		}
		
		SwingUtilities.invokeAndWait(() -> new MainWindow(c));
		
	}

	private static void start(String[] args) throws Exception {
		parseArgs(args);
		switch (_mode) {
		case BATCH:
			startBatchMode();
			break;
		case GUI:
			startGUIMode();
			break;
		}
		
	}

	public static void main(String[] args) {
		try {
			initFactories();
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}