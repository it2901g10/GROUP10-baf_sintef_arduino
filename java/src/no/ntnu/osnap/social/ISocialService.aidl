package no.ntnu.osnap.social;
 
interface ISocialService {
	 String[] request(String json, int model_type, int code);
}