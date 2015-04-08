/**
 * Copyright 2013 Alexander Erhard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aerhard.oxygen.framework.inplace;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;
import ro.sync.exml.workspace.api.Workspace;

/**
 * An dialog for editing one or more {@link Editable} objects
 * @author Alexander Erhard
 *
 */
@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class EditDialog extends OKCancelDialog {

    private static final long serialVersionUID = 1L;

    private JComponent[] fields;

    private Editable[] editables;

    public EditDialog(Workspace workspaceParam, String title,
            Editable[] editables) throws Exception {
        super((Frame) workspaceParam.getParentFrame(), title, true);

        ResourceBundle i18n = ResourceBundle.getBundle("Extensions");

        this.editables = editables;

        setLayout(new BorderLayout());

        JPanel labelPanel = new JPanel(new GridLayout(editables.length, 1, 10, 10));
        JPanel fieldPanel = new JPanel(new GridLayout(editables.length, 1, 10, 10));
        
        labelPanel.setBorder(new EmptyBorder(4, 0, 10, 4) );
        fieldPanel.setBorder(new EmptyBorder(4, 4, 10, 0) );
        
        getContentPane().add(labelPanel, BorderLayout.WEST);
        getContentPane().add(fieldPanel, BorderLayout.CENTER);

        fields = new JComponent[editables.length];

        for (int i = 0; i < fields.length; i += 1) {
            if (editables[i].isActive()) {
                if (editables[i].getPredefinedValues() != null
                        && editables[i].getPredefinedValueLabels() != null) {
                    fields[i] = new JComboBox(
                            editables[i].getPredefinedValueLabels());
                    ((JComboBox) fields[i]).setSelectedIndex(Arrays.asList(
                            editables[i].getPredefinedValues()).indexOf(
                            editables[i].getValue()));
                    
                } else {
                    fields[i] = new JTextField();
                    ((JTextField) fields[i]).setColumns(15);
                    ((JTextField) fields[i]).setText(editables[i].getValue());
                    ((JTextField) fields[i])
                            .addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    doOK();
                                }
                            });
                }
                createRow(fields[i], i18n.getString(editables[i].getLabel()),
                        labelPanel, fieldPanel);
            }
        }

        pack();
        setResizable(false);

    }

    private void createRow(JComponent fieldComponent, String labelText,
            JPanel labelPanel, JPanel fieldPanel) {
        JLabel labelComponent = new JLabel(labelText, JLabel.RIGHT);
        labelComponent.setLabelFor(fieldComponent);
        labelPanel.add(labelComponent);
        fieldPanel.add(fieldComponent);
    }

    Boolean submitData() {
        for (int i = 0; i < fields.length; i += 1) {
            if (editables[i].isActive()) {
                if (fields[i] instanceof JComboBox) {
                    editables[i]
                            .setValue(editables[i].getPredefinedValues()[((JComboBox) fields[i])
                                    .getSelectedIndex()]);
                } else if (fields[i] instanceof JTextField) {
                    editables[i].setValue(((JTextField) fields[i]).getText());
                }
            }
        }
        return true;
    }

    public Boolean showDialog() {
        setVisible(true);
        return (getResult() == RESULT_OK) ? submitData() : false;
    }

}