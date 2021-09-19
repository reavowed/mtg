import {render} from "preact";
import {useRef} from "preact/compat";
import {useCallback, useContext, useEffect, useState} from "preact/hooks";
import {Modal} from "react-bootstrap";
import GameState from "../../../contexts/GameState";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import CardBack from "../card/CardBack";
import CardWithText from "../card/CardWithText";
import DecisionButton from "../DecisionButton";
import PopupChoice from "./PopupChoice";
import _ from "lodash";
import $ from "jquery";

function insertOrMove(array, item, index) {
    const before = remove(array.slice(0, index),item);
    const after = remove(array.slice(index), item);
    return [...before, item, ...after];
}
function remove(array, item) {
    return _.filter(array, x => x !== item);
}

export default function ScryChoice() {
    const gameState = useContext(GameState);
    let library = gameState.libraries[gameState.currentChoice.playerToAct];
    const visibleCards = _.fromPairs(_.map(
        gameState.currentChoice.details.cardsBeingScryed,
        id => [id, _.find(library, o => o.objectId === id)]));

    const [cardsOnTop, setCardsOnTop] = useState(gameState.currentChoice.details.cardsBeingScryed);
    const [cardsOnBottom, setCardsOnBottom] = useState([]);
    const cardRefs = useRef({});
    const [cardBeingDragged, setCardBeingDragged] = useState(null);
    const [dragLocation, setDragLocation] = useState(null);

    const onDocumentMouseUp = useCallback((event) => {
        if (cardBeingDragged) {
            setCardBeingDragged(null);
            event.stopPropagation();
            event.preventDefault();
        }
    }, [cardBeingDragged]);
    const onDocumentMouseMove = useCallback((event) => {
        setDragLocation({x: event.pageX, y: event.pageY});

        function isBefore(cardId) {
            return event.pageX < $(cardRefs.current[cardId]).offset().left;
        }

        const cardsToCheck = _.filter([...cardsOnTop, "topCardBack", ...cardsOnBottom]);
        const insertionPoint = _.dropWhile(cardsToCheck, isBefore)[0];

        console.log(insertionPoint);

        if (insertionPoint === cardBeingDragged.id) {
        } else if (_.includes(cardsOnBottom, insertionPoint)) {
            const index = cardsOnBottom.indexOf(insertionPoint);
            setCardsOnBottom(existing => insertOrMove(existing, cardBeingDragged.id, index));
            setCardsOnTop(existing => remove(existing, cardBeingDragged.id));
        } else if (_.includes(cardsOnTop, insertionPoint)) {
            const index = cardsOnTop.indexOf(insertionPoint);
            setCardsOnTop(existing => insertOrMove(existing, cardBeingDragged.id, index));
            setCardsOnBottom(existing => remove(existing, cardBeingDragged.id));
        } else if (!insertionPoint) {
            setCardsOnBottom(existing => insertOrMove(existing, cardBeingDragged.id, existing.length));
            setCardsOnTop(existing => remove(existing, cardBeingDragged.id));
        } else if (insertionPoint === "topCardBack") {
            setCardsOnTop(existing => insertOrMove(existing, cardBeingDragged.id, existing.length));
            setCardsOnBottom(existing => remove(existing, cardBeingDragged.id));
        }
    }, [cardsOnTop, cardsOnBottom, cardBeingDragged]);
    useEffect(() => {
        if (cardBeingDragged) {
            $(document).on("mouseup", onDocumentMouseUp);
            $(document).on("mousemove", onDocumentMouseMove);
            return () => {
                $(document).off("mouseup", onDocumentMouseUp);
                $(document).off("mousemove", onDocumentMouseMove);
            }
        }
    }, [cardBeingDragged, onDocumentMouseUp, onDocumentMouseMove])

    const setCardRef = useCallback((ref, id) => {
        cardRefs.current[id] = ref;
    }, []);
    const onCardMouseDown = useCallback((event, id) => {
        setCardBeingDragged({
            id,
            x: event.pageX - $(cardRefs.current[id]).offset().left,
            y: event.pageY - $(cardRefs.current[id]).offset().top,
        });
        setDragLocation({x: event.pageX, y: event.pageY});
        event.stopPropagation();
        event.preventDefault();
    }, []);

    const numberOfCardBacksToDisplay = Math.min(library.length, 5);

    function renderCard(id, index) {
        const isBeingDragged = cardBeingDragged && cardBeingDragged.id === id;
        return <CardWithText key={id}
                             ref={ref => setCardRef(ref, id)}
                             onMouseDown={!isBeingDragged && (event => onCardMouseDown(event, id))}
                             card={visibleCards[id]}
                             className="cardOverlap"
                             style={{zIndex: index, visibility: isBeingDragged && "hidden"}} />;
    }

    const dragIndicator = cardBeingDragged && <CardWithText card={visibleCards[cardBeingDragged.id]}
                                                            style={{
                                                                zIndex: 100,
                                                                position: "fixed",
                                                                top: (dragLocation.y - cardBeingDragged.y) + "px",
                                                                left: (dragLocation.x - cardBeingDragged.x) + "px"
                                                            }} />;

    return <PopupChoice text="Scry">
        <HorizontalCenter>
            {dragIndicator}
            {_.map(_.reverse([...cardsOnBottom]), renderCard)}
            {_.map(Array(numberOfCardBacksToDisplay), (_, index) => <CardBack ref={ref => (index === numberOfCardBacksToDisplay - 1) && setCardRef(ref, "topCardBack") }
                                                                              key={"card-back-" + index}
                                                                              className="cardOverlap"
                                                                              style={{zIndex: cardsOnBottom.length + index}} />)}
            {_.map(_.reverse([...cardsOnTop]), (id, index) => renderCard(id, index + cardsOnBottom.length + numberOfCardBacksToDisplay))}
        </HorizontalCenter>
        <BannerText>Scry</BannerText>
        <HorizontalCenter>
            <DecisionButton optionToChoose={cardsOnTop.join(" ") + " | " + cardsOnBottom.join(" ")}>Submit</DecisionButton>
        </HorizontalCenter>
    </PopupChoice>
}
