import Entity from "../../../../mapper/annotations/Entity";
import AbstractEntity from "../../../../domain/container/AbstractEntity";
import Basic from "../../../../mapper/annotations/Basic";

@Entity("thumbnail")
export default class Thumbnail extends AbstractEntity {

    $type = "thumbnail"

    @Basic()
    name : string

    @Basic()
    type : string

    @Basic()
    subType : string

    @Basic()
    data : string

}