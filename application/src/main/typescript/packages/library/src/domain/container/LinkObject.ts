import Entity from "../../mapper/annotations/Entity";
import Basic from "../../mapper/annotations/Basic";

@Entity("link")
export default class LinkObject {

    $type = "link"

    @Basic()
    url : string

    @Basic()
    method : string

    @Basic()
    rel : string

    @Basic()
    title : string

    @Basic()
    linkType : string

}