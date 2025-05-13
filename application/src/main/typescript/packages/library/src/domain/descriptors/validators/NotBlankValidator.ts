import Validator from "./Validator";
import Entity from "../../../mapper/annotations/Entity";

@Entity("notBlankValidator")
export default class NotBlankValidator implements Validator {}