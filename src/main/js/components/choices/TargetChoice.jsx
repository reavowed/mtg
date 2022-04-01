import _ from "lodash";
import {useCallback, useContext, useEffect, useState} from "preact/hooks";
import ActionManager from "../../contexts/ActionManager";
import GameState from "../../contexts/GameState";
import BannerText from "../layoutUtils/BannerText";
import HorizontalCenter from "../layoutUtils/HorizontalCenter";
import DecisionButton from "../DecisionButton";

export default function TargetChoice() {
    const gameState = useContext(GameState);
    const actionManager = useContext(ActionManager);

    const choice = gameState.currentChoice.details;
    const [chosenTarget, setChosenTarget] = useState(null);

    const onClick = useCallback((objectId) => {
        if (chosenTarget === objectId) {
            setChosenTarget(null);
        } else if (_.includes(choice.validOptions, objectId)) {
            setChosenTarget(objectId);
        }
    }, [chosenTarget, gameState]);
    const getClasses = useCallback((objectId) => {
        if (chosenTarget === objectId) {
            return ["selected"];
        } else if (_.includes(choice.validOptions, objectId)) {
            return ["selectable"];
        } else {
            return [];
        }
    }, [chosenTarget, gameState]);
    useEffect(() => {
        actionManager.setActionHandler(() => onClick);
        actionManager.setClassGetter(() => getClasses);
        return () => {
            actionManager.setActionHandler(null);
            actionManager.setClassGetter(null);
        }
    }, [onClick, getClasses]);

    return <div>
        <BannerText as="p">Choose {choice.targetDescription}.</BannerText>
        <HorizontalCenter>
            <DecisionButton optionToChoose={chosenTarget && chosenTarget.toString()} disabled={!chosenTarget}>Submit</DecisionButton>
        </HorizontalCenter>
    </div>;
}
