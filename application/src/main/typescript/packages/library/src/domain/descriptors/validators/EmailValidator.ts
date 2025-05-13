import Validator from "./Validator";
import Entity from "../../../mapper/annotations/Entity";

@Entity("emailValidator")
export default class EmailValidator implements Validator {}