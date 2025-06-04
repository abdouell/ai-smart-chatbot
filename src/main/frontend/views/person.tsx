import {AutoCrud} from "@vaadin/hilla-react-crud";
import {PersonService} from "Frontend/generated/endpoints";
import PersonModel from "Frontend/generated/com/chatbot/aifaqchatbot/model/PersonModel";

export default function Person() {
    return(
      <AutoCrud service={PersonService} model={PersonModel}></AutoCrud>
    );
}
