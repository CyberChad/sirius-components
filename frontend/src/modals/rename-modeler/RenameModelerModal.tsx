/*******************************************************************************
 * Copyright (c) 2021 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
import { useMutation } from '@apollo/client';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import TextField from '@material-ui/core/TextField';
import gql from 'graphql-tag';
import PropTypes from 'prop-types';
import React, { useEffect, useState } from 'react';

const renameModelerMutation = gql`
  mutation renameModeler($input: RenameModelerInput!) {
    renameModeler(input: $input) {
      __typename
      ... on RenameModelerSuccessPayload {
        modeler {
          id
          name
          status
        }
      }
      ... on ErrorPayload {
        message
      }
    }
  }
`;

const propTypes = {
  modelerId: PropTypes.string.isRequired,
  initialModelerName: PropTypes.string.isRequired,
  onModelerRenamed: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};

export const RenameModelerModal = ({ modelerId, initialModelerName, onModelerRenamed, onClose }) => {
  const initialState = {
    name: initialModelerName,
    isNameValid: false,
    error: '',
    isValid: false,
  };
  const [state, setState] = useState(initialState);
  const { name, isValid } = state;

  const onNewName = (event) => {
    const newName = event.target.value;

    setState(() => {
      let isNameValid = newName && newName.length >= 1;
      let error = '';
      if (!isNameValid) {
        error = 'The name is required';
      }
      return {
        name: newName,
        isNameValid,
        error,
        isValid: isNameValid,
      };
    });
  };

  const [renameModeler, { loading, data, error }] = useMutation(renameModelerMutation);
  useEffect(() => {
    if (!loading) {
      if (error) {
        setState((prevState) => {
          const { name, isNameValid } = prevState;
          return { name, isNameValid, error: error.message, isValid: false };
        });
      } else if (data?.renameModeler) {
        const { renameModeler } = data;
        if (renameModeler.__typename === 'RenameModelerSuccessPayload') {
          onModelerRenamed();
        } else if (renameModeler.__typename === 'ErrorPayload') {
          const error = renameModeler.message;
          setState((prevState) => {
            const { name, isNameValid } = prevState;
            return { name, isNameValid, error, isValid: false };
          });
        }
      }
    }
  }, [loading, data, error, onModelerRenamed]);

  const onRenameModeler = (event) => {
    event.preventDefault();
    const input = {
      modelerId: modelerId,
      newName: name,
    };
    renameModeler({ variables: { input } });
  };

  return (
    <Dialog open={true} onClose={onClose} aria-labelledby="form-dialog-title">
      <DialogTitle id="dialog-title">Rename the modeler</DialogTitle>
      <DialogContent>
        <DialogContentText></DialogContentText>
        <TextField
          autoFocus
          label="Name"
          placeholder="Enter the new modeler name"
          value={name}
          onChange={onNewName}
          data-testid="name"
          fullWidth
        />
      </DialogContent>
      <DialogActions>
        <Button
          variant="contained"
          disabled={!isValid}
          onClick={onRenameModeler}
          color="primary"
          data-testid="rename-modeler">
          Rename
        </Button>
      </DialogActions>
    </Dialog>
  );
};
RenameModelerModal.propTypes = propTypes;
