import NodeDescriptor from "./NodeDescriptor";
import Basic from "../../mapper/annotations/Basic";
import Entity from "../../mapper/annotations/Entity";

@Entity("enumDescriptor")
export default class EnumDescriptor extends NodeDescriptor {

    $type = "enumDescriptor"

    @Basic()
    enums : string[]


}