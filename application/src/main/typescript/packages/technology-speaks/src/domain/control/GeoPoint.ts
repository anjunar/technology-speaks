import {ActiveObject, Basic, Entity} from "react-ui-simplicity";

@Entity("geoPoint")
export default class GeoPoint extends ActiveObject {

    $type = "geoPoint"

    @Basic()
    x : number

    @Basic()
    y : number

}