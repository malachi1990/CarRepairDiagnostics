package com.ubiquisoft.evaluation;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.ConditionType;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

import java.util.List;
import java.util.Map;

public class CarDiagnosticEngine {

	public void executeDiagnostics(Car car) {
		/*
		 * Implement basic diagnostics and print results to console.
		 *
		 * The purpose of this method is to find any problems with a car's data or parts.
		 *
		 * Diagnostic Steps:
		 *      First   - Validate the 3 data fields are present, if one or more are
		 *                then print the missing fields to the console
		 *                in a similar manner to how the provided methods do.
		 *
		 *      Second  - Validate that no parts are missing using the 'getMissingPartsMap' method in the Car class,
		 *                if one or more are then run each missing part and its count through the provided missing part method.
		 *
		 *      Third   - Validate that all parts are in working condition, if any are not
		 *                then run each non-working part through the provided damaged part method.
		 *
		 *      Fourth  - If validation succeeds for the previous steps then print something to the console informing the user as such.
		 * A damaged part is one that has any condition other than NEW, GOOD, or WORN.
		 *
		 * Important:
		 *      If any validation fails, complete whatever step you are actively one and end diagnostics early.
		 *
		 * Treat the console as information being read by a user of this application. Attempts should be made to ensure
		 * console output is as least as informative as the provided methods.
		 */
		boolean successfulValidation = false;
		if(validateMakeModelInfo(car)) {
			if(validateMissingParts(car)) {
				successfulValidation = validateWorkingParts(car.getParts());
			}
		}

		if(successfulValidation) {
			System.out.println("All diagnostic steps completed.");
		}
	}


	private boolean validateMakeModelInfo(Car car) {
		boolean hasValidInfo = true;
		if(car.getMake().isEmpty()) {
			System.out.println("Car make is required");
			hasValidInfo = false;
		}

		if(car.getModel().isEmpty()) {
			System.out.println("Car model is required");
			hasValidInfo = false;
		}

		if(car.getYear().isEmpty()) {
			System.out.println("Car year is required");
			hasValidInfo = false;
		}

		if(!hasValidInfo) {
			System.out.println("Missing required information, diagnostic ending.");
		}
		return hasValidInfo;
	}

	private boolean validateMissingParts(Car car) {
		boolean hasAllParts = true;
		Map<PartType, Integer> missingParts = car.getMissingPartsMap();
		if(!missingParts.isEmpty()) {
			hasAllParts = false;
			for(Map.Entry<PartType, Integer> part : missingParts.entrySet()) {
				printMissingPart(part.getKey(), part.getValue());
			}
		}

		if(!hasAllParts) {
			System.out.println("Missing required parts, diagnostic ending.");
		}
		return hasAllParts;
	}


	private boolean validateWorkingParts(List<Part> parts) {
		boolean partsWorking = true;
		for(Part part : parts) {
			if(!part.isInWorkingCondition()) {
				partsWorking = false;
				printDamagedPart(part.getType(), part.getCondition());
			}
		}
		if(!partsWorking) {
			System.out.println("At least one part damaged, diagnostic ending.");
		}
		return partsWorking;
	}
	private void printMissingPart(PartType partType, Integer count) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (count == null || count <= 0) throw new IllegalArgumentException("Count must be greater than 0");

		System.out.println(String.format("Missing Part(s) Detected: %s - Count: %s", partType, count));
	}

	private void printDamagedPart(PartType partType, ConditionType condition) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (condition == null) throw new IllegalArgumentException("ConditionType must not be null");

		System.out.println(String.format("Damaged Part Detected: %s - Condition: %s", partType, condition));
	}

	public static void main(String[] args) throws JAXBException {
		// Load classpath resource
		InputStream xml = ClassLoader.getSystemResourceAsStream("SampleCar.xml");

		// Verify resource was loaded properly
		if (xml == null) {
			System.err.println("An error occurred attempting to load SampleCar.xml");

			System.exit(1);
		}

		// Build JAXBContext for converting XML into an Object
		JAXBContext context = JAXBContext.newInstance(Car.class, Part.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		Car car = (Car) unmarshaller.unmarshal(xml);

		// Build new Diagnostics Engine and execute on deserialized car object.

		CarDiagnosticEngine diagnosticEngine = new CarDiagnosticEngine();

		diagnosticEngine.executeDiagnostics(car);

	}

}
