import {Entity} from "../../mapper";
import ActiveObject from "./ActiveObject";
import Basic from "../../mapper/annotations/Basic";

@Entity("sort")
export default class SortObject extends ActiveObject {

    $type = "sort"

    @Basic()
    property : string

    @Basic()
    value : string

}