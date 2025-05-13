import Validator from "./Validator";
import Entity from "../../../mapper/annotations/Entity";

@Entity("pastValidator")
export default class PastValidator implements Validator {}