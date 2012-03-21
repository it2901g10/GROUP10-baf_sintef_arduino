package no.ntnu.osnap.social;

//import no.ntnu.osnap.social.mockupModel;
 
interface ISocialService {
	 String[] request(in String json, int model_type, int code);
	//mockupModel noop(in Model a);
}