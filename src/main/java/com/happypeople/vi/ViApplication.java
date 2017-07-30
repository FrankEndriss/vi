package com.happypeople.vi;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ViApplication {
	private final static Logger log=LoggerFactory.getLogger(ViApplication.class);

	public static void main(final String[] args) {

		// TODO get rid of spring in startup, to slow.
		final ConfigurableApplicationContext context=
				new SpringApplicationBuilder(ViApplication.class).headless(false).run(args);

		try {
			runTheApp(context, args);
		}catch(final Exception e) {
			log.error("some error, main thread ended: ", e);
		}
	}

	private static OptionSet parseArgs(final String[] args) {
		final OptionParser parser=new OptionParser();
		parser.acceptsAll(Arrays.asList("R", "readonly"), "readonly mode, no write to file possible"); // readonly
		parser.accepts("w"); // one window per file/editor
		parser.accepts("t"); // one window and one tab within that window per file/editor

		return parser.parse(args);
	}

	public static void runTheApp(final ConfigurableApplicationContext context, final String[] args) throws IOException {
		final OptionSet cliArgs=parseArgs(args);

		final Properties globalProps=new Properties();
		globalProps.put("argc", args.length);
		for(int i=0; i<args.length; i++)
			globalProps.put("argv["+i+"]", args[i]);
		final GlobalConfig globalConfig=new GlobalConfig() {
			@Override
			public Optional<String> getValue(final String key) {
				return Optional.ofNullable(globalProps.getProperty(key));
			}
		};

		final EditContextBuilder builder=new EditContextBuilder();
		builder.setGlobalConfig(globalConfig).readonly(true);

		if(!cliArgs.nonOptionArguments().isEmpty())
			builder.file(new File(args[0]));

		final EditContext editContext=builder.build();

		// TODO think about how to run an editContext, especially if there is more than one
		editContext.run();

	}

}
