import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";

@Entity("chunkTableSearch")
export default class ChunkSearch extends AbstractSearch {

    $type = "chunkTableSearch"

    @Basic()
    document : string

}