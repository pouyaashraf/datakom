package datakom;

import java.awt.TextArea;

public class EditPane extends TextArea {

	private static String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam tristique lectus et nisl tincidunt, in tempus ligula semper. Ut convallis augue in pharetra porttitor. Aliquam vestibulum pretium elementum. Nulla facilisi. Vestibulum nibh lorem, efficitur vel consequat quis, pellentesque non eros. Aliquam luctus ante vitae quam rhoncus efficitur. Integer fermentum, est quis aliquet elementum, purus nunc sagittis dui, id pulvinar libero augue sed elit. Suspendisse potenti.\n" +
			"Donec feugiat justo sollicitudin augue pellentesque, at finibus eros fermentum. Aliquam mauris libero, consequat ac purus in, ullamcorper tincidunt elit. Morbi et iaculis mi. Nunc at lectus eget nisl vehicula molestie. Proin tristique felis eget metus fringilla aliquet. Duis luctus, velit quis venenatis ullamcorper, nulla lorem sollicitudin massa, vitae pellentesque nunc leo vitae sapien. Integer lacinia tincidunt tempus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam erat volutpat. Nulla in porttitor justo. Sed luctus ipsum et malesuada venenatis. Curabitur vel nisl sit amet tellus egestas scelerisque. Aenean lacinia consequat posuere. Ut vitae leo ac quam convallis maximus. Fusce finibus viverra dui.\n" +
			"Pellentesque est ipsum, tempor sed mauris id, aliquet mollis nibh. Donec in pharetra ex, at posuere odio. Mauris at cursus nulla, in maximus velit. In placerat lectus sapien, quis rhoncus leo faucibus non. Etiam non sollicitudin nibh, vel vulputate sapien. Nunc lacinia nibh erat, vel dignissim turpis consectetur quis. Sed ex leo, molestie ac dui eu, mollis egestas purus. Aenean ornare libero quis libero scelerisque rutrum. Mauris commodo turpis a arcu ultricies, sed blandit metus viverra. Mauris bibendum augue vitae odio sagittis, id pretium nulla fringilla. Donec a arcu ut ipsum commodo accumsan.\n" +
			"Curabitur vel elementum ex. Praesent et est tortor. Vestibulum ultricies tincidunt velit nec cursus. Nam gravida mauris nec mi tincidunt, ac laoreet ligula tincidunt. Nunc gravida vulputate ipsum, ut lobortis urna volutpat eu. In hac habitasse platea dictumst. Nam suscipit suscipit consequat. Nunc ut fermentum nisl. Morbi nisi urna, vestibulum quis venenatis vitae, ultricies vel neque. Integer id sem ut ipsum euismod semper vel at ipsum. Aenean sodales egestas accumsan. Suspendisse ultricies, velit sed eleifend semper, purus dui commodo diam, nec pellentesque leo arcu eget nunc. Interdum et malesuada fames ac ante ipsum primis in faucibus. Praesent congue lobortis metus vel tempus.\n" +
			"Praesent et congue magna. Integer egestas porta odio, vel tincidunt lectus posuere vel. Curabitur cursus pretium venenatis. Donec pretium sapien nulla, at eleifend ante maximus a. Nullam ullamcorper, ex non bibendum vestibulum, dui enim facilisis ligula, ut sodales leo odio eget justo. Quisque condimentum tristique libero eu scelerisque. Aliquam magna erat, malesuada vel tempus quis, viverra ac orci. Proin a sem et lorem commodo luctus a quis ante. Proin sed erat vulputate, congue magna id, condimentum quam.";
		
	public EditPane() {
		super(30, 60);
		setText(lorem);
		// TODO Auto-generated constructor stub
	}

}
