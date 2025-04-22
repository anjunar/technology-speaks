import Entity from "../../mapper/annotations/Entity";
import Basic from "../../mapper/annotations/Basic";

@Entity("Link")
export default class LinkObject {

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