import Entity from "../../mapper/annotations/Entity";
import Basic from "../../mapper/annotations/Basic";
import type LinkContainerObject from "./LinkContainerObject";
import ActiveObject from "./ActiveObject";
import LinksObject from "./LinksObject";

@Entity("QueryTable")
export default class QueryTableObject<S, R> extends ActiveObject implements LinksObject {

    @Basic()
    rows : R[]

    @Basic()
    size : number

    @Basic()
    links : LinkContainerObject

    @Basic()
    search : S

}