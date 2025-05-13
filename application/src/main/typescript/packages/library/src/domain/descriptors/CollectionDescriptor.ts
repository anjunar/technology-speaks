import NodeDescriptor from "./NodeDescriptor";
import Basic from "../../mapper/annotations/Basic";
import Entity from "../../mapper/annotations/Entity";
import ObjectDescriptor from "./ObjectDescriptor";

@Entity("collectionDescriptor")
export default class CollectionDescriptor extends NodeDescriptor    {

    $type = "collectionDescriptor"

    @Basic()
    items : ObjectDescriptor

}