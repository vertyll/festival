import styled, { keyframes } from "styled-components";

const fadeIn = keyframes`
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
`;

const HoverText = styled.div`
  display: none;
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  background-color: var(--hover-text-color);
  color: var(--light-text-color);
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 0.8em;
  white-space: nowrap;
  animation: ${fadeIn} 0.3s ease-in-out;
`;

export default HoverText;
