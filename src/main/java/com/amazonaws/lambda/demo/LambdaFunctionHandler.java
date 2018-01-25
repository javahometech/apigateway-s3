package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class LambdaFunctionHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		JSONParser parser = new JSONParser();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		try {
			JSONObject event = (JSONObject) parser.parse(reader);
			context.getLogger().log(event.toJSONString());
			byte[] decode = Base64.getDecoder().decode(event.get("body-json").toString().getBytes());
			ByteArrayInputStream bis = new ByteArrayInputStream(decode);
			AmazonS3 s3client = AmazonS3ClientBuilder
					  .standard()
					  .build();
			s3client.putObject("javahome-upload", "data.xlsx", bis, new ObjectMetadata());
			context.getLogger().log(event.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		int letter = 0;
		while ((letter = input.read()) >= 0) {
			output.write(Character.toUpperCase(letter));
		}
	}

}
