import {AbstractEntity, Basic, Entity} from "react-ui-simplicity";

@Entity("chunk")
export default class Chunk extends AbstractEntity {

    $type = "chunk"

    @Basic()
    title : string

    @Basic()
    content : string

}