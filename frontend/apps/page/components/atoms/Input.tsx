import styled from "styled-components";

const Input = styled.input`
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid var(--main-night-color);
  border-radius: 20px;
  box-sizing: border-box;
  background-color: var(--main-white-smoke-color);

  &:hover {
    border: 1px solid var(--main-medium-slate-blue-color);
  }
`;

export default Input;
