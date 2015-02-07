package com.zakarie.colors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class CommandAdapter extends ArrayAdapter<Command> {
    private Context context;
    private int layout;

    private List<Command> commands;

    private ColorSingleton colorSingleton;

    public CommandAdapter(Context context, int layout, List<Command> commands) {
        super(context, layout, commands);

        this.context = context;
        this.layout = layout;
        this.commands = commands;
        this.colorSingleton = ColorSingleton.getInstance();

        // Insert the default value
        AbsoluteCommand defaultAC = new AbsoluteCommand(new byte[]{ColorSingleton.COLOR_DEFAULT, ColorSingleton.COLOR_DEFAULT, ColorSingleton.COLOR_DEFAULT});
        commands.add(defaultAC);

        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View commandView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (commandView == null) {
            commandView = LayoutInflater.from(context).inflate(layout, parent, false);
        }

        Command command = commands.get(position);

        TextView type = (TextView) commandView.findViewById(R.id.command_type);
        type.setText(command.getCommandType());

        TextView red = (TextView) commandView.findViewById(R.id.command_red_value);
        red.setText(Integer.toString(command.getR()));

        TextView green = (TextView) commandView.findViewById(R.id.command_green_value);
        green.setText(Integer.toString(command.getG()));

        TextView blue = (TextView) commandView.findViewById(R.id.command_blue_value);
        blue.setText(Integer.toString(command.getB()));

        CheckBox commandSelected = (CheckBox) commandView.findViewById(R.id.command_selected);
        commandSelected.setChecked(command.isSelected());

        return commandView;
    }

    private void deselectAll() {
        for (Command command : commands) {
            if (command.isSelected()) {
                command.toggleSelected();
            }
        }
    }

    private void onSelect(Command command) {
        // Use the run-time type to dispatch the associated method
        if (command instanceof RelativeCommand) {
            colorSingleton.update((RelativeCommand) command);
        } else if (command instanceof AbsoluteCommand) {
            // As per the selection rules, only keep this command selected
            deselectAll();
            command.toggleSelected();

            colorSingleton.update((AbsoluteCommand) command);
        }
    }

    private void onDeselect(Command command) {
        // Use the run-time type to dispatch the associated method
        if (command instanceof RelativeCommand) {
            colorSingleton.revert((RelativeCommand) command);
        } else if (command instanceof AbsoluteCommand) {
            // Absolute commands can only be deselected by selecting another absolute command
            command.toggleSelected();

            colorSingleton.revert((AbsoluteCommand) command);
        }
    }

    @Override
    public synchronized void add(Command command) {
        commands.add(command);

        onSelect(command);

        notifyDataSetChanged();
    }

    public synchronized void toggleSelection(int position) {
        Command command = commands.get(position);
        command.toggleSelected();

        if (command.isSelected()) {
            onSelect(command);
        } else {
            onDeselect(command);
        }

        notifyDataSetChanged();
    }
}
