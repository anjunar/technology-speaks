import Validator from "./Validator";
import Entity from "../../../mapper/annotations/Entity";

@Entity("notNullValidator")
export default class NotNullValidator implements Validator {}