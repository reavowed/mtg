import {useContext} from "preact/hooks";
import GameState from "../../../GameState";
import BannerText from "../../layout/BannerText";
import ScreenCenter from "../../layout/ScreenCenter";
import Hand from "../Hand";
import MulliganChoice from "./MulliganChoice";
import ReturnCardsToLibraryChoice from "./ReturnCardsToLibraryChoice";

function OpeningHandWhileWaiting() {
    return <div>
        <Hand />
        <BannerText>Waiting for your opponent to make a mulligan decision</BannerText>
    </div>
}

export default function OpeningHand() {
    const gameState = useContext(GameState);
    const decision = gameState.currentChoice.playerToAct !== gameState.player ? <OpeningHandWhileWaiting/> :
        gameState.currentChoice.type === "MulliganChoice" ? <MulliganChoice /> :
        gameState.currentChoice.type === "ReturnCardsToLibraryChoice" ? <ReturnCardsToLibraryChoice /> :
        <Hand />;
    return <ScreenCenter>
        <div className="w-100">
            {decision}
        </div>
    </ScreenCenter>;
}
