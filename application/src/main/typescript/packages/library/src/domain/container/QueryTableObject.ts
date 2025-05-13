import Entity from "../../mapper/annotations/Entity";
import Basic from "../../mapper/annotations/Basic";
import type LinkContainerObject from "./LinkContainerObject";
import ActiveObject from "./ActiveObject";
import LinksObject from "./LinksObject";

@Entity("queryTable")
export default class QueryTableObject<S, R> extends ActiveObject implements LinksObject {

    $type = "queryTable"

    @Basic()
    rows : R[]

    @Basic()
    size : number

    @Basic()
    links : LinkContainerObject

    @Basic()
    search : S

}